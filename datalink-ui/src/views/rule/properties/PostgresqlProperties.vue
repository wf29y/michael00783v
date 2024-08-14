<template>
  <a-row :gutter='20'>
    <a-form-model layout='vertical' :model='properties'>
      <a-col :span='24' class='sql'>
        <a-form-model-item label='SQL模板' v-if="type==='dest'" style='margin-bottom: 0'>
          <codemirror  v-model='properties.sql' :options='options'
                       style='border:  1px #e8e3e3 solid'></codemirror>
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
  components:{codemirror},
  data() {
    return {
      properties: {},
      options: {
        mode: { name: 'text/x-sql', json: true },
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
      console.log(properties)
      this.properties = properties
    },
    get() {
      console.log(this.properties)
      return this.properties
    }
  }
}
</script>

<style>

.sql .cm-s-base16-light.CodeMirror {
  background: white !important;
  color: #202020;
}

.sql .cm-s-base16-light span.cm-comment {
  font-size: 13px;
}

.sql .cm-s-base16-light .CodeMirror-activeline-background {
  background: #f3f2f2;
}

.sql .CodeMirror {
  height: 200px;
}

.sql .CodeMirror-scroll {
  height: 200px;
}

</style>
