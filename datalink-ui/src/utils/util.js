export function timeFix() {
  const time = new Date()
  const hour = time.getHours()
  return hour < 9 ? '早上好' : hour <= 11 ? '上午好' : hour <= 13 ? '中午好' : hour < 20 ? '下午好' : '晚上好'
}

export function welcome() {
  const arr = ['休息一会儿吧', '准备吃什么呢?', '要不要打一把 DOTA', '我猜你可能累了']
  const index = Math.floor(Math.random() * arr.length)
  return arr[index]
}

/**
 * 触发 window.resize
 */
export function triggerWindowResizeEvent() {
  const event = document.createEvent('HTMLEvents')
  event.initEvent('resize', true, true)
  event.eventType = 'message'
  window.dispatchEvent(event)
}

export function handleScrollHeader(callback) {
  let timer = 0

  let beforeScrollTop = window.pageYOffset
  callback = callback || function() {
  }
  window.addEventListener(
    'scroll',
    event => {
      clearTimeout(timer)
      timer = setTimeout(() => {
        let direction = 'up'
        const afterScrollTop = window.pageYOffset
        const delta = afterScrollTop - beforeScrollTop
        if (delta === 0) {
          return false
        }
        direction = delta > 0 ? 'down' : 'up'
        callback(direction)
        beforeScrollTop = afterScrollTop
      }, 50)
    },
    false
  )
}

export function isIE() {
  const bw = window.navigator.userAgent
  const compare = (s) => bw.indexOf(s) >= 0
  const ie11 = (() => 'ActiveXObject' in window)()
  return compare('MSIE') || ie11
}

/**
 * Remove loading animate
 * @param id parent element id or class
 * @param timeout
 */
export function removeLoadingAnimate(id = '', timeout = 1500) {
  if (id === '') {
    return
  }
  setTimeout(() => {
    document.body.removeChild(document.getElementById(id))
  }, timeout)
}

export function scorePassword(pass) {
  let score = 0
  if (!pass) {
    return score
  }
  // award every unique letter until 5 repetitions
  const letters = {}
  for (let i = 0; i < pass.length; i++) {
    letters[pass[i]] = (letters[pass[i]] || 0) + 1
    score += 5.0 / letters[pass[i]]
  }

  // bonus points for mixing it up
  const variations = {
    digits: /\d/.test(pass),
    lower: /[a-z]/.test(pass),
    upper: /[A-Z]/.test(pass),
    nonWords: /\W/.test(pass)
  }

  let variationCount = 0
  for (var check in variations) {
    variationCount += (variations[check] === true) ? 1 : 0
  }
  score += (variationCount - 1) * 10

  return parseInt(score)
}

export function formatJson(json, options) {
  let reg = null,
    formatted = '',
    pad = 0,
    PADDING = '  '
  options = options || {}
  options.newlineAfterColonIfBeforeBraceOrBracket = (options.newlineAfterColonIfBeforeBraceOrBracket === true) ? true : false
  options.spaceAfterColon = (options.spaceAfterColon === false) ? false : true
  if (typeof json !== 'string') {
    json = JSON.stringify(json)
  } else {
    json = JSON.parse(json)
    json = JSON.stringify(json)
  }
  reg = /([\{\}])/g
  json = json.replace(reg, '\r\n$1\r\n')
  reg = /([\[\]])/g
  json = json.replace(reg, '\r\n$1\r\n')
  reg = /(\,)/g
  json = json.replace(reg, '$1\r\n')
  reg = /(\r\n\r\n)/g
  json = json.replace(reg, '\r\n')
  reg = /\r\n\,/g
  json = json.replace(reg, ',')
  if (!options.newlineAfterColonIfBeforeBraceOrBracket) {
    reg = /\:\r\n\{/g
    json = json.replace(reg, ':{')
    reg = /\:\r\n\[/g
    json = json.replace(reg, ':[')
  }
  if (options.spaceAfterColon) {
    reg = /\:/g
    json = json.replace(reg, ':')
  }
  (json.split('\r\n')).forEach(function(node, index) {
    //console.log(node);
    var i = 0,
      indent = 0,
      padding = ''

    if (node.match(/\{$/) || node.match(/\[$/)) {
      indent = 1
    } else if (node.match(/\}/) || node.match(/\]/)) {
      if (pad !== 0) {
        pad -= 1
      }
    } else {
      indent = 0
    }

    for (i = 0; i < pad; i++) {
      padding += PADDING
    }

    formatted += padding + node + '\r\n'
    pad += indent
  })
  return formatted.replace('\r\n', '')
}