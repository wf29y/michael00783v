<template>
  <div>
    <a-card title='解析脚本' :body-style='{paddingBottom:0}'>

      <div slot='extra'>
        <a-button :style="{ marginRight: '8px' }" @click='onClose' icon='close'> 取消</a-button>
        <a-button type='primary' @click='saveScript' icon='save'> 保存</a-button>
      </div>

      <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>

        <a-row :gutter='20'>
          <a-col :span='8' v-show='modal.scriptId'>
            <a-form-model-item label='ID' prop='scriptId'>
              <a-input v-model='modal.scriptId' placeholder='请输入脚本ID' :disabled='true'></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span='modal.scriptId?8:12'>
            <a-form-model-item label='名称' prop='scriptName'>
              <a-input v-model='modal.scriptName' placeholder='请输入脚本名称'></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span='modal.scriptId?8:12'>
            <a-form-model-item label='说明' prop='description'>
              <a-input v-model='modal.description' placeholder='请输入脚本说明'></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span='24' class='scriptModel'>
            <a-form-model-item label='JavaScript脚本' prop='scriptContent'>
              <codemirror v-model='modal.scriptContent' :options='options'
                          style='border:  1px #e8e3e3 solid'></codemirror>
            </a-form-model-item>
          </a-col>
        </a-row>

      </a-form-model>
    </a-card>
  </div>


</template>

<script>
import { getAction, postAction, putAction } from '@/api/manage'
import { codemirror } from 'vue-codemirror-lite'

require('codemirror/mode/javascript/javascript')
require('codemirror/mode/vue/vue')
require('codemirror/addon/hint/show-hint.js')
require('codemirror/addon/hint/show-hint.css')
require('codemirror/addon/hint/javascript-hint.js')
require('codemirror/theme/base16-light.css')
require('codemirror/addon/selection/active-line')


export default {
  name: 'ResourceModel',
  components: { codemirror },
  props: {
    scriptId: {
      type: String,
      default: undefined
    }
  },
  data() {
    return {
      title: '操作',
      visible: false,
      confirmLoading: false,
      modal: {
        scriptContent:'/**\n' +
          ' * 方法名transform不可修改,入参：data Object 源数据,出参：data Object 目标数据\n' +
          ' */\n' +
          'function transform(data) {\n' +
          '    return data;\n' +
          '}'
      },
      url: {
        info: '/api/script/info',
        add: '/api/script/add',
        update: '/api/script/update'
      },
      rules: {
        scriptName: [{ required: true, message: '请输入脚本名称', trigger: 'blur' }],
        scriptContent: [{ required: true, message: '请输入脚本内容', trigger: 'blur' }]
      },
      options: {
        mode: { name: 'text/javascript', json: true },
        height: 400,
        lineNumbers: true,
        tabSize: 2,
        theme: 'base16-light',
        line: true,
        autoCloseTags: true,
        lineWrapping: true,
        styleActiveLine: true,
        extraKeys: { 'Ctrl-Space': 'autocomplete' }, //自定义快捷键
        hintOptions: {
          tables: {}
        }
      }
    }
  },
  mounted() {
    if (this.scriptId) {
      getAction(this.url.info, { scriptId: this.scriptId }).then(res => {
        this.modal = res.data
      })
    }
  },
  methods: {
    saveScript() {
      const that = this
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          that.confirmLoading = true
          let script = JSON.parse(JSON.stringify(this.modal))
          let obj
          if (this.scriptId) {
            obj = putAction(this.url.update, script)
          } else {
            obj = postAction(this.url.add, script)
          }
          obj.then(res => {
            if (res.code === 200) {
              that.$message.success(res.message)
              that.onClose()
            } else {
              that.$message.warning(res.message)
            }
          })
            .finally(() => {
              that.confirmLoading = false
              that.onClose()
            })

        } else {
          return false
        }
      })
    },
    onClose() {
      this.$router.push({ name: 'scriptList' })
    }
  }
}
</script>

<style>


.scriptModel .cm-s-base16-light.CodeMirror {
  background: white !important;
  color: #202020;
}

.scriptModel .cm-s-base16-light span.cm-comment {
  font-size: 13px;
}

.scriptModel .cm-s-base16-light .CodeMirror-activeline-background {
  background: #f3f2f2;
}

.scriptModel .CodeMirror {
  height: 400px;
}

.scriptModel .CodeMirror-scroll {
  height: 400px;
}


</style>
