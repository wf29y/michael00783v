<template>
  <a-modal
    title='解析脚本'
    :width='900'
    :visible='visible'
    @cancel='onClose'
    :bodyStyle='{padding:0}'
    :footer="null"
  >

    <div class='scriptView'>

      <codemirror :options='options' v-model='script'></codemirror>

    </div>



  </a-modal>
</template>

<script>
import { codemirror } from 'vue-codemirror-lite'

require('codemirror/mode/javascript/javascript')
require('codemirror/mode/vue/vue')
require('codemirror/addon/hint/show-hint.js')
require('codemirror/addon/hint/show-hint.css')
require('codemirror/addon/hint/javascript-hint.js')
require('codemirror/theme/base16-light.css')
require('codemirror/addon/selection/active-line')



export default {
  name: 'ScriptViewModel',
  components: {codemirror},
  data() {
    return {
      visible: false,
      script: "",
      options: {
        mode: { name: 'text/javascript', json: true },
        height: 150,
        lineNumbers: true,
        tabSize: 2,
        theme: 'base16-light',
        line: true,
        autoCloseTags: true,
        lineWrapping: true,
        readOnly:'nocursor',
        extraKeys: { 'tab': 'autocomplete' }, //自定义快捷键
        hintOptions: {
          tables: {}
        }
      },
    }
  },
  methods: {
    show(script) {
      this.script =script
      this.visible = true
    },
    onClose() {
      this.visible = false
      this.script = ""
    },
  }
}
</script>

<style>



.cm-s-base16-light.CodeMirror {
  background: white !important;
  color: #202020;
}

.cm-s-base16-light span.cm-comment {
  font-size: 13px;
}

.cm-s-base16-light .CodeMirror-activeline-background {
  background: #f3f2f2;
}

.scriptView .CodeMirror {
  height: 500px;
}

.scriptView .CodeMirror-scroll {
  height: 500px;
}


</style>
