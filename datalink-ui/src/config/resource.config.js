const TYPE_ALL = 'all'   // 可做任意资源
const TYPE_DEST = 'dest' // 仅可做目的资源
const TYPE_RESOURCE = 'resource'  // 仅可做源资源

const timeUnitMap = {
  SECONDS: '秒',
  MINUTES: '分',
  HOURS: '时',
  DAYS: '天'
}

const resourceConfigMap = {
  MQTT: {
    name: 'MQTT Broker',
    type: TYPE_ALL,
    details: {
      resource: { name: '资源地址', format: (resource) => resource.properties.url },
      rule: [
        { name: '资源地址', format: (resource) => resource.properties.url },
        { name: 'Topic', format: (resource) => resource.properties.topic }
      ]
    }
  },
  KAFKA: {
    name: 'Kafka',
    type: TYPE_ALL,
    details: {
      resource: { name: '资源地址', format: (resource) => `${resource.properties.url}` },
      rule: [
        { name: '资源地址', format: (resource) => `${resource.properties.url}` },
        { name: 'Topic', format: (resource) => resource.properties.topic }
      ]
    }
  },
  MYSQL: {
    name: 'Mysql',
    type: TYPE_DEST,
    details: {
      resource: { name: '资源地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '资源地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
        { name: 'SQL模板', format: (resource) => resource.properties.sql }
      ]
    }
  },
  POSTGRESQL: {
    name: 'Postgresql',
    type: TYPE_DEST,
    details: {
      resource: { name: '资源地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '资源地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
        { name: 'SQL模板', format: (resource) => resource.properties.sql }
      ]
    }
  },
  HTTP: {
    name: 'HTTP',
    type: TYPE_ALL,
    details: {
      resource: { name: '资源地址', format: (resource) => `${resource.properties.url}` },
      rule: [
        { name: '请求路径', format: (resource) => `${resource.properties.url}${resource.properties.path}` },
        { name: '请求方式', format: (resource) => `${resource.properties.method}` },
        { name: '启动延迟', format: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined },
        { name: '调用频率', format: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined }
      ]
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

const resourceTypeMap = createTypeMap()

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
  if (!resource || !resource.resourceType) {
    if (mode === 'resource') {
      return { name: '', value: '' }
    } else {
      return [{ name: '', value: '' }]
    }
  }
  let resourceConfigMapElement = resourceConfigMap[resource.resourceType]
  let detail = resourceConfigMapElement.details[mode]
  if (mode === 'resource') {
    let value = detail.format(resource)
    if (value) {
      return {
        name: detail.name,
        value: value
      }
    } else {
      return { name: '', value: '' }
    }
  } else {
    let result = []
    for (let item of detail) {
      let value = item.format(resource)
      if (value) {
        result.push({
          name: item.name,
          value: value
        })
      }
    }
    return result
  }

}


export {
  resourceTypeMap, getResourceTypeList, getResourceDetails
}
