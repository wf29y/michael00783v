<template>
  <div ref='codeContainer' :style='{ width: width, height: height ,border: border?"1px #e8e3e3 solid":"0" }'></div>
</template>

<script>
import { setLocaleData } from 'monaco-editor-nls'
import zh_CN from 'monaco-editor-nls/locale/zh-hans'

setLocaleData(zh_CN)
const monaco = require('monaco-editor/esm/vs/editor/editor.api')

export default {
  props: {
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '200px'
    },
    language: {
      type: String,
      default: 'javascript'
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    border: {
      type: Boolean,
      default: true
    },
    minimap: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      monacoEditor: null
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      if (this.monacoEditor !== null) return
      this.monacoEditor = monaco.editor.create(this.$refs.codeContainer, {
        value: '', // 默认显示的值
        language: this.language,
        folding: true, // 是否折叠
        foldingHighlight: true, // 折叠等高线
        foldingStrategy: 'indentation', // 折叠方式  auto | indentation
        showFoldingControls: 'mouseover', // 是否一直显示折叠 always | mouseover
        disableLayerHinting: true, // 等宽优化
        emptySelectionClipboard: false, // 空选择剪切板
        selectionClipboard: false, // 选择剪切板
        automaticLayout: true, // 自动布局
        codeLens: false, // 代码镜头
        scrollBeyondLastLine: false, // 滚动完最后一行后再滚动一屏幕
        colorDecorators: true, // 颜色装饰器
        accessibilitySupport: 'on', // 辅助功能支持  "auto" | "off" | "on"
        lineNumbers: 'on', // 行号 取值： "on" | "off" | "relative" | "interval" | function
        lineNumbersMinChars: 5, // 行号最小字符   number
        enableSplitViewResizing: false,
        readOnly: this.readOnly, //是否只读  取值 true | false
        wordWrap: 'on', // 自动换行
        minimap: {
          enabled: this.minimap // 是否启用预览图
        }
      })
    },
    set(value) {
      if(!value) return
      if (this.monacoEditor === null) {
        this.$nextTick(() => {
          this.init()
          this.monacoEditor.setValue(value)
        })
      } else {
        this.monacoEditor.setValue(value)
      }
    },
    get() {
      return this.monacoEditor.getValue()
    }
  }
}
</script>