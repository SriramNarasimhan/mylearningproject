import static com.day.cq.commons.jcr.JcrUtil.createValue

def updateMultipleProperty(property, from, to) {
    modified = false
    newValues = property.values.collect { oldValue ->
        if (oldValue.string.contains(from)) {
            modified = true
            return createValue(oldValue.string.replace(from, to), session)
        } else {
            return oldValue
        }
    } as Value[]
    if (modified) {
        property.setValue(newValues)
        println("Update property " + property.path)
    }
}

def updateProperty(property, from, to) {
    if (property.type != PropertyType.STRING) {
        return;
    } else if (property.multiple) {
        updateMultipleProperty(property, from, to)
    } else {
        if (property.string.contains(from)) {
            property.setValue(property.string.replace(from, to))
            println("Update property " + property.path)
        }
    }
}

def updateContent(root, from, to, properties = null) {
    getNode(root).recurse { node ->
        node.properties
        .findAll { property ->
            properties == null || properties.contains(property.name)
        }
        .each { property ->
            updateProperty(property, from, to)
        }
    }
    session.save()
}

// example usage:
// updateContent("/content/geometrixx", "square", "triangle")