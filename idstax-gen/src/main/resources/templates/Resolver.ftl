//
// Generated with Cthul ID-StAX Gen ${version}
//

package ${package};

import org.ctuhl.xml.idstax.AbstractElementIDResolver;

/**
 * Element resolver for ${name} schema.
 */
public class ${u.CamelCase(name)}Resolver
        extends AbstractElementIDResolver
        implements ${u.CamelCase(name)}IDs {

    public ${u.CamelCase(name)}Resolver() {
        init();
    }

    protected void init() {
<#list schema.namespaces as ns>
    <#if ns.hasElements()>
        namespace(${ns.constant})
        <#list ns.enames as en>
                .add("${en.name}", ${en.constant})
        </#list>
                ;
    </#if>
</#list>
    }

}

