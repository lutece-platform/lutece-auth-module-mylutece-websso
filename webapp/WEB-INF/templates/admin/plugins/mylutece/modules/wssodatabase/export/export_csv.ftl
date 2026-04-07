<#ftl strip_whitespace=true>
<#function escape val>
    <#local s = val?string>
    <#if s?contains(";") || s?contains('"') || s?contains("\n")>
        <#return '"' + s?replace('"', '""') + '"'>
    </#if>
    <#return s>
</#function><#--
--><#list users as user><#--
User -->${escape(user.guid!)}<#--
-->;${escape(user.lastName!)}<#--
-->;${escape(user.firstName!)}<#--
-->;${escape(user.email!)}<#--
Roles --><#if user.roles??><#list user.roles as role>;${escape("role:" + role)}</#list></#if><#--
Profils --><#if user.profils??><#list user.profils as profil>;${escape("profil:" + profil)}</#list></#if><#--
--><#if user_has_next>${"\n"}</#if></#list>
