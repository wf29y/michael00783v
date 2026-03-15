import { timeUnitMap } from './time.config'

const resourceGroupMap = {
  CHANNEL: '消息通道',
  PROTOCOL: '通讯协议',
  DATABASE: '数据存储'
}

const resourceConfigMap = {
  MQTT: {
    name: 'MQTT',
    type: 'all',
    group: 'CHANNEL',
    details: {
      resource: { name: '地址', value: (resource) => resource.properties.url },
      rule: [
        { name: '地址', value: (resource) => resource.properties.url },
        { name: 'Topic', value: (resource) => resource.properties.topic }
      ]
    }
  },
  TCP: {
    name: 'TCP',
    type: 'source',
    group: 'PROTOCOL',
    details: {
      resource: { name: '监听地址', value: (resource) => `0.0.0.0:${resource.properties.port}` },
      rule: [
        { name: '监听地址', value: (resource) => `0.0.0.0:${resource.properties.port}` },
        {
          name: '响应内容',
          value: (resource) => resource.properties.response ? `${resource.properties.response}` : undefined
        }
      ]
    }
  },
  UDP: {
    name: 'UDP',
    type: 'source',
    group: 'PROTOCOL',
    details: {
      resource: { name: '监听地址', value: (resource) => `0.0.0.0:${resource.properties.port}` },
      rule: [
        { name: '监听地址', value: (resource) => `0.0.0.0:${resource.properties.port}` },
        {
          name: '响应内容',
          value: (resource) => resource.properties.response ? `${resource.properties.response}` : undefined
        }
      ]
    }
  },
  SNMP: {
    name: 'SNMP',
    type: 'source',
    group: 'PROTOCOL',
    details: {
      resource: { name: '地址', value: (resource) => `udp:${resource.properties.ip}/${resource.properties.port}` },
      rule: [
        { name: '地址', value: (resource) => `udp:${resource.properties.ip}/${resource.properties.port}` },
        { name: '读取点位', value: (resource) => `${resource.properties.points ? resource.properties.points.length : 0}` },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '读取频率',
          value: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
      ]
    }
  },
  KAFKA: {
    name: 'Kafka',
    type: 'all',
    group: 'CHANNEL',
    details: {
      resource: { name: '地址', value: (resource) => `${resource.properties.url}` },
      rule: [
        { name: '地址', value: (resource) => `${resource.properties.url}` },
        { name: 'Topic', value: (resource) => resource.properties.topic }
      ]
    }
  },
  RABBITMQ: {
    name: 'RabbitMQ',
    type: 'all',
    group: 'CHANNEL',
    details: {
      resource: { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
        { name: '虚拟主机', value: (resource) => resource.properties.virtualHost },
        { name: '交换机', value: (resource) => resource.properties.exchange },
        { name: '队列', value: (resource) => resource.properties.queue }
      ]
    }
  },
  ROCKETMQ: {
    name: 'RocketMQ',
    type: 'all',
    group: 'CHANNEL',
    details: {
      resource: { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
        { name: '分组', value: (resource) => resource.properties.group },
        { name: 'Topic', value: (resource) => resource.properties.topic }
      ]
    }
  },
  ACTIVEMQ: {
    name: 'ActiveMQ',
    type: 'all',
    group: 'CHANNEL',
    details: {
      resource: { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
        {
          name: (resource) => resource.properties.model === 'queue' ? 'Queue' : 'Topic',
          value: (resource) => resource.properties.model === 'queue' ? resource.properties.queue : resource.properties.topic
        }
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
        value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
          value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', value: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '查询频率',
          value: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
      ]
    }
  },
  MARIADB: {
    name: 'MariaDB',
    type: 'all',
    group: 'DATABASE',
    details: {
      resource: {
        name: '地址',
        value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
          value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', value: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '查询频率',
          value: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
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
        value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
          value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', value: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '查询频率',
          value: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
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
        value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
          value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', value: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '查询频率',
          value: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
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
        value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
          value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', value: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '查询频率',
          value: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
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
        value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
      },
      rule: [
        {
          name: '地址',
          value: (resource) => `${resource.properties.ip}:${resource.properties.port}/${resource.properties.databaseName}`
        },
        { name: 'SQL模板', value: (resource) => resource.properties.sql },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '查询频率',
          value: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
      ]
    }
  },
  HTTPCLIENT: {
    name: 'HTTP Client',
    type: 'all',
    group: 'PROTOCOL',
    details: {
      resource: { name: '地址', value: (resource) => `${resource.properties.url}` },
      rule: [
        { name: '请求路径', value: (resource) => `${resource.properties.url}${resource.properties.path}` },
        { name: '请求方式', value: (resource) => `${resource.properties.method}` },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '调用频率',
          value: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
      ]
    }
  },
  HTTPSERVER: {
    name: 'HTTP Server',
    type: 'source',
    group: 'PROTOCOL',
    details: {
      resource: {
        name: '监听地址',
        value: (resource) => `http://0.0.0.0:${resource.properties.port}${resource.properties.path}`
      },
      rule: [
        { name: '监听地址', value: (resource) => `http://0.0.0.0:${resource.properties.port}${resource.properties.path}` },
        {
          name: '响应内容',
          value: (resource) => resource.properties.response ? `${resource.properties.response}` : undefined
        }
      ]
    }
  },
  OPCUA: {
    name: 'OPC UA',
    type: 'source',
    group: 'PROTOCOL',
    details: {
      resource: { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
        { name: '读取点位', value: (resource) => `${resource.properties.points ? resource.properties.points.length : 0}` },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '读取频率',
          value: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        }
      ]
    }
  },
  MODBUSTCP: {
    name: 'Modbus TCP',
    type: 'source',
    group: 'PROTOCOL',
    details: {
      resource: { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
      rule: [
        { name: '地址', value: (resource) => `${resource.properties.ip}:${resource.properties.port}` },
        { name: '读取点位', value: (resource) => `${resource.properties.points ? resource.properties.points.length : 0}` },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '读取频率',
          value: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
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
        value: (resource) => resource.properties.mode === 'STANDALONE' ? `${resource.properties.ip}:${resource.properties.port}` : `${resource.properties.nodes}`
      },
      rule: [
        {
          name: '地址',
          value: (resource) => resource.properties.mode === 'STANDALONE' ? `${resource.properties.ip}:${resource.properties.port}` : `${resource.properties.nodes}`
        },
        { name: '执行命令', value: (resource) => `${resource.properties.command}` },
        {
          name: '启动延迟',
          value: (resource) => resource.properties.initialDelay ? `${resource.properties.initialDelay}${timeUnitMap[resource.properties.timeUnit]}` : undefined
        },
        {
          name: '执行频率',
          value: (resource) => resource.properties.period ? `${resource.properties.period}${timeUnitMap[resource.properties.timeUnit]}` : undefined
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

const emptyDetail = { name: '', value: '' }

function getResourceDetails(resource, mode) {
  if (!resource || !resource.resourceType) {
    return mode === 'resource' ? emptyDetail : [emptyDetail]
  }
  let resourceConfigMapElement = resourceConfigMap[resource.resourceType]
  let detail = resourceConfigMapElement.details[mode]
  if (mode === 'resource') {
    return toResultItem(detail, resource)
  } else {
    return detail.map((item) => toResultItem(item, resource))
  }
}

function toResultItem(detailItem, resource) {
  let name = typeof detailItem.name === 'string' ? detailItem.name : detailItem.name(resource)
  if (!name) return emptyDetail
  let value = typeof detailItem.value === 'string' ? detailItem.value : detailItem.value(resource)
  if (!value) return emptyDetail
  return { name: name, value: value }
}


export {
  resourceTypeMap, resourceTypeAllList, getResourceListByType, getResourceListByTypeAndGroup, getResourceDetails
}
