package com.platform.plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.IPlugin;
import com.platform.constant.ConstantRender;
import com.platform.mvc.base.BaseModel;
import com.platform.tools.ToolCache;
import com.platform.tools.ToolClassSearch;
import com.platform.tools.ToolDirFile;
import com.platform.tools.ToolSqlXml;

/**
 * 加载sql文件
 *
 * @author 董华健
 */
public class SqlMarkDownPlugin implements IPlugin {

    protected final static Logger log = LoggerFactory.getLogger(SqlMarkDownPlugin.class);

    private final static String lineSeparator = System.getProperty("line.separator", "\n");

    private static String selectHeader;

    /**
     * sql放入cache中时的key前缀
     */
    public static final String cacheStart_sql = "sql_";

    public SqlMarkDownPlugin() {
    }

    @Override
    public boolean start() {
        init(true);
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    /**
     * 加载sql语句到cache
     *
     * @param isInit 是否初始化加载
     */
    public static synchronized void init(boolean isInit) {
        //兼容原始无select头的写法
        selectHeader = ToolSqlXml.getSqlMy(BaseModel.sqlId_splitPageSelect, null, ConstantRender.sql_renderType_beetl);

        // 1.查找classes目录
        String classesPath = ToolDirFile.getClassesPath();
        List<File> sqlMDFiles = new ArrayList<File>();
        findByClasses(new File(classesPath), sqlMDFiles);
        for (File file : sqlMDFiles) {
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                readSqlFile(file.getName(), in);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        //
                    }
                }
            }
        }

        // 2.查找jar文件中的目录
        findByJars(isInit);
    }

    /**
     * 递归查找classes中的.sql.xml文件
     *
     * @param dirFile
     * @param sqlMDFiles
     */
    private static void findByClasses(File dirFile, List<File> sqlMDFiles) {
        if (!dirFile.isDirectory()) {
            if (dirFile.getName().endsWith(".md")) {
                sqlMDFiles.add(dirFile);
            }
        } else {
            File[] fileList = dirFile.listFiles();
            if (fileList == null || fileList.length == 0) {
                return;
            }
            for (File file : fileList) {
                if (file.isDirectory()) {
                    findByClasses(file, sqlMDFiles);

                } else {
                    if (file.getName().endsWith(".md")) {
                        sqlMDFiles.add(file);
                    }
                }
            }
        }
    }

    /**
     * 查找jar文件中的.sql.xml文件
     *
     * @param isInit
     */
    private static void findByJars(boolean isInit) {
        try {
            // jar中文件查找
            List<String> jarList = ToolClassSearch.getScanJarList();
            int size = jarList.size();
            for (String aJarList : jarList) {
                JarFile jarFile = new JarFile(new File(ToolDirFile.getLibPath() + File.separator + aJarList));
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String entryName = jarEntry.getName();
                    String pkgEntryName = entryName.replaceAll("/", ".");

                    // 去除不需要扫描的包
                    List<String> pkgs = ToolClassSearch.getScanPkgList();
                    boolean pkgResult = false;
                    for (String pkg : pkgs) {
                        if (pkgEntryName.startsWith(pkg)) {
                            pkgResult = true;
                            break;
                        }
                    }

                    // 查找.md文件
                    if (!jarEntry.isDirectory() && pkgResult && entryName.endsWith(".md")) {
                        InputStream inputStream = null;
                        try {
                            inputStream = jarFile.getInputStream(jarEntry);
                            readSqlFile(jarFile.getName(), inputStream);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        }
                    }
                }
                jarFile.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Copy from  org.beetl.sql.core.ClasspathLoader#readSqlFile(java.lang.String, java.io.InputStream)
     *
     * @param id
     * @param ins
     * @return
     */
    private static boolean readSqlFile(String id, InputStream ins) {
        String modelName = id.substring(0, id.lastIndexOf(".") + 1);
        if (ins == null) return false;
//			InputStream ins  = null;
//		try{
//			ins = new FileInputStream(file);
//		}catch(IOException ioe){
//			throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL, "未找到[id="+id+"]相关SQL"+id,ioe);
//		}
//        Integer lastModified = ins.hashCode();
//		long lastModified = file.lastModified();
//        sqlSourceVersion.put(id, lastModified);
//		InputStream ins = this.getClass().getResourceAsStream(
//				sqlRoot + File.separator + modelName + "md");
        LinkedList<String> list = new LinkedList<String>();
        BufferedReader bf = null;
        try {

            bf = new BufferedReader(new InputStreamReader(ins, "utf-8"));
            String temp = null;
            StringBuilder sql = null;
            String key = null;
            int lineNum = 0;
            int findLineNum = 0;
            while ((temp = bf.readLine()) != null) {
                temp = temp.trim();
                lineNum++;
                if (temp.startsWith("===")) {// 读取到===号，说明上一行是key，下面是注释或者SQL语句
                    if (!list.isEmpty() && list.size() > 1) {// 如果链表里面有多个，说明是上一句的sql+下一句的key
                        String tempKey = list.pollLast();// 取出下一句sql的key先存着
                        sql = new StringBuilder();
                        key = list.pollFirst();
                        while (!list.isEmpty()) {// 拼装成一句sql
                            sql.append(list.pollFirst()).append(lineSeparator);
                        }
                        String sqlStr = sql.toString().trim().replaceAll("[\\s]{2,}", " ");
                        sqlStr = StringUtils.replaceOnce(sqlStr, selectHeader, "").trim();
                        ToolCache.set(cacheStart_sql + modelName + key, sqlStr);
                        list.addLast(tempKey);// 把下一句的key又放进来
                        findLineNum = lineNum;
                    }
                    boolean sqlStart = false;
                    String tempNext = null;
                    while ((tempNext = bf.readLine()) != null) {//处理注释的情况
                        tempNext = tempNext.trim();
                        lineNum++;
                        if (tempNext.startsWith("*") || (StringUtils.isBlank(tempNext) && !sqlStart)) {//读到注释行，不做任何处理
                            continue;
                        } else {
                            sqlStart = true;
                            list.addLast(tempNext);//===下面不是*号的情况，是一条sql
                            break;//读到一句sql就跳出循环
                        }
                    }
                } else {
                    list.addLast(temp);
                }
            }
            // 最后一句sql
            sql = new StringBuilder();
            key = list.pollFirst();
            while (!list.isEmpty()) {
                sql.append(list.pollFirst()).append(lineSeparator);
            }
            String sqlStr = sql.toString().trim().replaceAll("[\\s]{2,}", " ");
            sqlStr = StringUtils.replace(sqlStr, selectHeader, "").trim();
            ToolCache.set(cacheStart_sql + modelName + key, sqlStr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }


}