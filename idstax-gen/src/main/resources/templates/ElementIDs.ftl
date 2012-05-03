//
// Generated with Cthul ID-StAX Gen ${version}
//

package ${package};

/**
 * Element IDs for ${name} schema.
 */
public interface ${u.CamelCase(name)}IDs {

<#list schema.namespaces as ns>
    <#if ns.hasElements()>
    /**
     * Namespace <#if !ns.isDefault()>${ns.abbrev()} </#if>"${ns.uri}"
     */
    public static final String ${ns.constant} = "${ns.uri}";

    </#if>
</#list>
<#list schema.enames as en>
    /**
     * ID of ${en.typeString} "${en.ns_name()}"
     */
    public static final int ${en.constant} = ${en.id};

</#list>
}

