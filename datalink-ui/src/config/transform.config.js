const transformModeMap = {
  WITHOUT: '透传',
  SQL: 'SQL转换',
  SCRIPT: '脚本转换',
  PLUGIN: '插件转换',
}

function createModeList() {
  let result = []
  for (let key in transformModeMap) {
    result.push({
      name: transformModeMap[key],
      value: key
    })
  }
  return result
}

const transformModeList = createModeList()
export {
  transformModeMap, transformModeList
}
