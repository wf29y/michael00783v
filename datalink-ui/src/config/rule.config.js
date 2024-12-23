const analysisModeMap = {
  WITHOUT: '无解析透传',
  SCRIPT: 'JavaScript脚本',
  PLUGIN: 'Java插件(暂不支持)'
}

function createModeList() {
  let result = []
  for (let key in analysisModeMap) {
    result.push({
      name: analysisModeMap[key],
      value: key
    })
  }
  return result
}

const analysisModeList = createModeList()
export {
  analysisModeMap, analysisModeList
}
