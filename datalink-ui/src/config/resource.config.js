const TYPE_ALL = 'all'   // 可做任意资源
const TYPE_DEST = 'dest' // 仅可做目的资源
const TYPE_RESOURCE = 'resource'  // 仅可做源资源

const resourceConfigMap = {
  MQTT: {
    name: 'MQTT Broker',
    type: TYPE_ALL,
    details: {
      resource: { name: '资源地址', format: (resource) => resource.properties.url },
      rule: { name: 'Topic', format: (resource) => resource.properties.topic }
    },
    dataFormat:
`{
  "topic":String,
  "payload":String
}`
  },
  MYSQL: {
    name: 'Mysql',
    type: TYPE_DEST,
    details: {
      resource: { name: '资源地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: { name: 'SQL模板', format: (resource) => resource.properties.sql } // todo 截取 加 省略号
    }
  },
  POSTGRESQL: {
    name: 'Postgresql',
    type: TYPE_DEST,
    details: {
      resource: { name: '资源地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: { name: 'SQL模板', format: (resource) => resource.properties.sql } // todo 截取 加 省略号
    }
  }
}


function createTypeMap() {
  let result = {}
  for (let resourceConfigMapKey in resourceConfigMap) {
    result[resourceConfigMapKey] = resourceConfigMap[resourceConfigMapKey].name
  }
  return result
}

function createDataFormatMap() {
  let result = {}
  for (let resourceConfigMapKey in resourceConfigMap) {
    let dataFormat = resourceConfigMap[resourceConfigMapKey].dataFormat
    if (!dataFormat) continue
    result[resourceConfigMapKey] = dataFormat
  }
  return result
}

const resourceTypeMap = createTypeMap()
const resourceDataFormatMap = createDataFormatMap()

function getResourceTypeList(type) {
  let result = []
  for (let resourceConfigMapKey in resourceConfigMap) {
    let resourceType = resourceConfigMap[resourceConfigMapKey].type
    if (!type || resourceType === TYPE_ALL || resourceType === type) {
      let item = {
        name: resourceConfigMap[resourceConfigMapKey].name,
        code: resourceConfigMapKey,
        type: resourceType
      }
      result.push(item)
    }
  }
  return result
}

function getResourceDetails(resource, mode) {
  if (!resource || !mode) return { name: '', value: '' }
  let resourceConfigMapElement = resourceConfigMap[resource.resourceType]
  let detail = resourceConfigMapElement.details[mode]
  return {
    name: detail.name,
    value: detail.format(resource)
  }
}


export {
  resourceTypeMap, resourceDataFormatMap, getResourceTypeList, getResourceDetails
}
