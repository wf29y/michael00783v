<template>
  <a-row :gutter='20'>
    <a-form-model layout='vertical' :model='properties'>
      <a-col :span='24'>
        <a-form-model-item :label='properties.dynamicTopic?"Topic模板":"Topic"'>
          <a-input v-model='properties.topic' placeholder='请输入Topic' />
        </a-form-model-item>
      </a-col>
      <a-col :span='8'>
        <a-form-model-item label='动态Topic' prop='dynamicTopic' v-if="type==='dest'">
          <a-select v-model='properties.dynamicTopic' placeholder='请选择是否动态Topic'>
            <a-select-option :value='true'>是</a-select-option>
            <a-select-option :value='false'>否</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='8'>
        <a-form-model-item label='消息保留' prop='retained' v-if="type==='dest'">
          <a-select v-model='properties.retained' placeholder='请选择是否消息保留'>
            <a-select-option :value='true'>是</a-select-option>
            <a-select-option :value='false'>否</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span="type==='dest'?8:24">
        <a-form-model-item label='Qos'>
          <a-input-number v-model='properties.qos' placeholder='请输入Qos' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='payload'>
        <a-form-model-item label='消息模板' v-if="type==='dest'" style='margin-bottom: 0'>
          <codemirror v-model='properties.template' :options='options' style='border:  1px #e8e3e3 solid'></codemirror>
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>
import { codemirror } from 'vue-codemirror-lite'

require('codemirror/mode/sql/sql.js')
require('codemirror/mode/vue/vue')
require('codemirror/addon/hint/show-hint.js')
require('codemirror/addon/hint/show-hint.css')
require('codemirror/theme/base16-light.css')
require('codemirror/addon/selection/active-line')

export default {
  components: { codemirror },
  data() {
    return {
      properties: {
        qos: 0,
        dynamicTopic: false,
        retained: false
      },
      options: {
        mode: { name: 'text/javascript', json: true },
        height: 200,
        lineNumbers: true,
        tabSize: 2,
        theme: 'base16-light',
        line: true,
        autoCloseTags: true,
        lineWrapping: true,
        styleActiveLine: true,
        extraKeys: { 'tab': 'autocomplete' }, //自定义快捷键
        hintOptions: {
          tables: {}
        }
      }
    }
  },
  props: {
    type: { // dest or source
      type: String,
      default: undefined
    }
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
    },
    get() {
      return this.properties
    }
  }
}
</script>

<style>

.payload .cm-s-base16-light.CodeMirror {
  background: white !important;
  color: #202020;
}

.payload .cm-s-base16-light span.cm-comment {
  font-size: 13px;
}

.payload .cm-s-base16-light .CodeMirror-activeline-background {
  background: #f3f2f2;
}

.payload .CodeMirror {
  height: 200px;
}

.payload .CodeMirror-scroll {
  height: 200px;
}

</style>
