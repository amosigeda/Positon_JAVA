loc.findAllPos
===
select * from bs_460 where 1=2 
<% for(it in items){%>
or ID like #'$it${itLP.index-1}$%'#
<%}%>
order by Radius

loc.findAllPosCDMA
===
select * from bs_cdma_460 where 1=2 
<% for(it in items){%>
or ID like #'$it${itLP.index-1}$%'#
<%}%>
order by Radius

loc.findAllPosForeign
===
select * from bs where 1=2 
<%for(it in items){%>
or ID like #'$it${itLP.index-1}$%'#
<%}%>
order by Radius

area.find
===
select * from country_area_code where 
(provinceCN like #'$province$%'# or provinceCN like #'$prov$%'#)
and (cn like #'$city$%'# or cn like #'$ct$%'#)