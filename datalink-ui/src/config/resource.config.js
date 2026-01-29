import { timeUnitMap } from './time.config'

const resourceGroupMap = {
  DATABASE: '数据库',
  CHANNEL: '消息通道',
  PROTOCOL: '通讯协议'
}

const resourceConfigMap = {
  MQTT: {
    name: 'MQTT Broker',
    type: 'all',
    group: 'CHANNEL',
    details: {
      resource: { name: '地址', format: (resource) => resource.properties.url },
      rule: [
        { name: '地址', format: (resource) => resource.properties.url },
        { name: 'Topic', format: (resource) => resource.properties.topic }
      ]
    }
  },
  TCP: {
    name: 'TCP',
    type: 'source',
    group: 'PROTOCOL',
    details: {
      resource: { name: '监听地址', format: (resource) => `0.0.0.0:${resource.properties.port}` },
      rule: [
        { name: '监听地址', format: (resource) => `0.0.0.0:${resource.properties.port}` },
        {
          name: '响应内容',
          format: (resource) => resource.properties.response ? `${resource.properties.response}` : undefined
        }
      ]
    }
  },
  UDP: {
    name: 'UDP',
    type: 'source',
    group: 'PROTOCOL',
    details: {
      resource: { name: '监听地址', format: (resource) => `0.0.0.0:${resource.properties.port}` },
      rule: [
        { name: '监听地址', format: (resource) => `0.0.0.0:${resource.properties.port}` },
        {
          name: '响应内容',
          format: (resource) => resource.properties.response ? `${resource.properties.response}` : undefined
        }
      ]
    }
  },
  SNMP: {
    name: 'SNMP',
    type: 'source',
    group: 'PROTOCOL',
    details: {
      resource: { name: '地址', format: (resource) => `udp:${resource.properties.ip}/${resource.properties.port}` },
      rule: [
        { name: '地址', format: (resource) => `udp:${resource.properties.ip}/${resource.properties.port}` },
        { name: '读取点位', format: (resource) => `${resource.properties.points ? resource.properties.points.length : 0}` },
        {
          name: '启动延迟',
          format: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '读取频率',
          format: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
      ]
    }
  },
  KAFKA: {
    name: 'Kafka',
    type: 'all',
    group: 'CHANNEL',
    details: {
      resource: { name: '地址', format: (resource) => `${resource.properties.url}` },
      rule: [
        { name: '地址', format: (resource) => `${resource.properties.url}` },
        { name: 'Topic', format: (resource) => resource.properties.topic }
      ]
    }
  },
  RABBITMQ: {
    name: 'RabbitMQ',
    type: 'all',
    group: 'CHANNEL',
    details: {
      resource: { name: '地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
        { name: '虚拟主机', format: (resource) => resource.properties.virtualHost },
        { name: '交换机', format: (resource) => resource.properties.exchange },
        { name: '队列', format: (resource) => resource.properties.queue }
      ]
    }
  },
  MYSQL: {
    name: 'MySQL',
    type: 'all',
    group: 'DATABASE',
    details: {
      resource: {
        name: '地址',
        format: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
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
    type: 'all',
    group: 'DATABASE',
    details: {
      resource: {
        name: '地址',
        format: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
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
  TIMESCALEDB: {
    name: 'TimescaleDB',
    type: 'all',
    group: 'DATABASE',
    details: {
      resource: {
        name: '地址',
        format: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
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
    type: 'all',
    group: 'DATABASE',
    details: {
      resource: {
        name: '地址',
        format: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
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
    type: 'all',
    group: 'DATABASE',
    details: {
      resource: {
        name: '地址',
        format: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
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
  HTTPCLIENT: {
    name: 'HTTP Client',
    type: 'all',
    group: 'PROTOCOL',
    details: {
      resource: { name: '地址', format: (resource) => `${resource.properties.url}` },
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
  HTTPSERVER: {
    name: 'HTTP Server',
    type: 'source',
    group: 'PROTOCOL',
    details: {
      resource: { name: '监听地址', format: (resource) => `http://0.0.0.0:${resource.properties.port}${resource.properties.path}` },
      rule: [
        { name: '监听地址', format: (resource) => `http://0.0.0.0:${resource.properties.port}${resource.properties.path}` },
        {
          name: '响应内容',
          format: (resource) => resource.properties.response ? `${resource.properties.response}` : undefined
        }
      ]
    }
  },
  OPCUA: {
    name: 'OPC UA',
    type: 'source',
    group: 'PROTOCOL',
    details: {
      resource: { name: '地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
        { name: '读取点位', format: (resource) => `${resource.properties.points ? resource.properties.points.length : 0}` },
        {
          name: '启动延迟',
          format: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '读取频率',
          format: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
      ]
    }
  },
  MODBUSTCP: {
    name: 'Modbus TCP',
    type: 'source',
    group: 'PROTOCOL',
    details: {
      resource: { name: '地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '地址', format: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
        { name: '读取点位', format: (resource) => `${resource.properties.points ? resource.properties.points.length : 0}` },
        {
          name: '启动延迟',
          format: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '读取频率',
          format: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
      ]
    }
  },
  REDIS: {
    name: 'Redis',
    type: 'all',
    group: 'DATABASE',
    details: {
      resource: {
        name: '地址',
        format: (resource) => resource.properties.mode === 'STANDALONE' ? `${resource.properties.ip}:${resource.properties.port}` : `${resource.properties.nodes}`
      },
      rule: [
        {
          name: '地址',
          format: (resource) => resource.properties.mode === 'STANDALONE' ? `${resource.properties.ip}:${resource.properties.port}` : `${resource.properties.nodes}`
        },
        { name: '执行命令', format: (resource) => `${resource.properties.command}` },
        {
          name: '启动延迟',
          format: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '执行频率',
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

function getResourceListByType(type) {
  let result = []
  for (let resourceGroupMapKey in resourceGroupMap) {
    let resourceTypeList = getResourceListByTypeAndGroup(type, resourceGroupMapKey)
    if (!resourceTypeList || resourceTypeList.length === 0) {
      continue
    }
    result.push({
      group: resourceGroupMap[resourceGroupMapKey],
      list: resourceTypeList
    })
  }
  return result
}

function getResourceListByTypeAndGroup(type, group) {
  let result = []
  for (let resourceConfigMapKey in resourceConfigMap) {
    let resourceConfig = resourceConfigMap[resourceConfigMapKey]
    if (type && resourceConfig.type !== type && resourceConfig.type !== 'all') {
      continue
    }
    if (group && resourceConfig.group !== group) {
      continue
    }
    let item = {
      name: resourceConfig.name,
      type: resourceConfig.type,
      code: resourceConfigMapKey
    }
    result.push(item)
  }
  return result
}

const resourceTypeAllList = getResourceListByType(undefined)

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
  resourceTypeMap, resourceTypeAllList, getResourceListByType, getResourceListByTypeAndGroup, getResourceDetails
}
