import javax.jcr.*
import org.apache.sling.api.resource.*

def ROOT_PATH = '/content/geometrixx'

def extractPaths(p) {
    if (p instanceof Property && p.multiple) {
        p.values.collect { extractPaths(it) }.flatten()
    } else {
        p.string.findAll(/\/content\/[^"]+/)
    }
}

getNode(ROOT_PATH).recurse { node ->
    node.properties.findAll {it.type == PropertyType.STRING}.each {
        paths = extractPaths(it).findAll { resourceResolver.resolve(it) instanceof NonExistingResource }
        if (!paths.empty) {
            println "Path:   ${node.path}"
            println "Broken: ${paths}\n"
        }
    }
}
true