const TYPE_ALL = 'all'   // 可做任意资源
const TYPE_DEST = 'dest' // 仅可做目的资源
const TYPE_SOURCE = 'source'  // 仅可做源资源

import { timeUnitMap } from './time.config'

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
  RABBITMQ: {
    name: 'RabbitMQ',
    type: TYPE_ALL,
    details: {
      resource: { name: '资源地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '资源地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
        { name: '虚拟主机', format: (resource) => resource.properties.virtualHost },
        { name: '交换机', format: (resource) => resource.properties.exchange },
        { name: '队列', format: (resource) => resource.properties.queue }
      ]
    }
  },
  MYSQL: {
    name: 'MySQL',
    type: TYPE_ALL,
    details: {
      resource: {
        name: '资源地址',
        format: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '资源地址',
          format: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', format: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          format: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '查询频率',
          format: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
      ]
    }
  },
  POSTGRESQL: {
    name: 'PostgreSQL',
    type: TYPE_ALL,
    details: {
      resource: {
        name: '资源地址',
        format: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '资源地址',
          format: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', format: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          format: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '查询频率',
          format: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
      ]
    }
  },
  SQLSERVER: {
    name: 'SQL Server',
    type: TYPE_ALL,
    details: {
      resource: {
        name: '资源地址',
        format: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '资源地址',
          format: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', format: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          format: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '查询频率',
          format: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
      ]
    }
  },
  TDENGINE: {
    name: 'TDengine',
    type: TYPE_ALL,
    details: {
      resource: {
        name: '资源地址',
        format: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '资源地址',
          format: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', format: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          format: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '查询频率',
          format: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
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
        {
          name: '启动延迟',
          format: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '调用频率',
          format: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
      ]
    }
  },
  OPCUA: {
    name: 'OPC UA',
    type: TYPE_SOURCE,
    details: {
      resource: { name: '资源地址', format: (resource) => `${resource.properties.url}` },
      rule: [
        { name: '资源地址', format: (resource) => `${resource.properties.url}` },
        { name: '读取点位', format: (resource) => `${resource.properties.points ? resource.properties.points.length : 0}` }
      ]
    }
  },
  REDIS: {
    name: 'Redis',
    type: TYPE_ALL,
    details: {
      resource: {
        name: '资源地址',
        format: (resource) => resource.properties.mode === 'STANDALONE' ? `${resource.properties.ip}:${resource.properties.port}` : `${resource.properties.nodes}`
      },
      rule: [
        {
          name: '资源地址',
          format: (resource) => resource.properties.mode === 'STANDALONE' ? `${resource.properties.ip}:${resource.properties.port}` : `${resource.properties.nodes}`
        },
        { name: '执行命令', format: (resource) => `${resource.properties.command}` },
        {
          name: '启动延迟',
          format: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '调用频率',
          format: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
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
