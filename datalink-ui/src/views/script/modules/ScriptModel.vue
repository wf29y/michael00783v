<template>
  <a-row :gutter='20'>
    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>
      <a-col :span='15'>
        <a-card title='脚本' :body-style='{paddingBottom:0}' :bordered='false'>
          <div slot='extra' style='padding: 0'>
            <a-popconfirm v-if='!disSaveBtn' title='放弃编辑的内容?' @confirm='() => {onClose()}'>
              <a-button :style="{ marginRight: '8px' }" icon='rollback'> 返回</a-button>
            </a-popconfirm>
            <a-button v-if='disSaveBtn' :style="{ marginRight: '8px' }" @click='onClose' icon='rollback'> 返回</a-button>
            <a-button type='primary' @click='saveScript' icon='save' :disabled='disSaveBtn'> 保存</a-button>
          </div>
          <a-row :gutter='20'>
            <a-col :span='9'>
              <a-form-model-item label='名称' prop='scriptName'>
                <a-input v-model='modal.scriptName' placeholder='请输入脚本名称'></a-input>
              </a-form-model-item>
            </a-col>
            <a-col :span='9'>
              <a-form-model-item label='说明' prop='description'>
                <a-input v-model='modal.description' placeholder='请输入脚本说明'></a-input>
              </a-form-model-item>
            </a-col>
            <a-col :span='6'>
              <a-form-model-item label='最后修改' prop='description'>
                <a-input v-model='modal.updateTime' placeholder='最后修改时间' :read-only='true'></a-input>
              </a-form-model-item>
            </a-col>
            <a-col :span='24' class='scriptModel'>
              <a-form-model-item label='JavaScript脚本' prop='scriptContent'>
                <codemirror v-model='modal.scriptContent' :options='options' style='border:  1px #e8e3e3 solid'
                            @change='contentChange'></codemirror>
              </a-form-model-item>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
      <a-col :span='9'>
        <a-card title='调试' :body-style='{paddingBottom:0}' :bordered='false'>
          <div slot='extra' style='padding: 0'>
            <a-button type='primary' @click='runScript' icon='caret-right' class='runBtn'
                      :disabled='!modal.paramContent'> 运行
            </a-button>
          </div>
          <a-row :gutter='20'>
            <a-col :span='24' class='inputModel'>
              <a-form-model-item label='输入参数（Json）' prop='paramContent'>
                <div style='margin-top: -30px;width: 100%;text-align: right;height: 30px;color: #000000;padding-top: 4px'>
                  <a @click='formatParam'><a-icon type="menu-unfold" /></a>
                </div>
                <codemirror v-model='modal.paramContent' :options='options' style='border:  1px #e8e3e3 solid'></codemirror>
              </a-form-model-item>
            </a-col>
            <a-col :span='24' class='outputModel'>
              <a-form-model-item label='运行结果' prop='resultContent'>
                <div style='margin-top: -30px;width: 100%;text-align: right;height: 30px;color: #000000'>
                  <span v-show='time >= 0' style='display: inline-block;padding-right: 15px'>用时：{{ time }}ms</span>
                  <a @click='formatResult'><a-icon type="menu-unfold" /></a>
                </div>
                <codemirror v-model='modal.resultContent' :options='options' style='border:  1px #e8e3e3 solid'></codemirror>
              </a-form-model-item>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-form-model>
  </a-row>


</template>

<script>
import { getAction, postAction, putAction } from '@/api/manage'
import { formatJson } from '@/utils/util'
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
  data() {
    return {
      title: '操作',
      visible: false,
      confirmLoading: false,
      modal: {
        scriptContent: '/**\n' +
          '* 方法名transform不可修改,入参：data Object 源数据,出参：data Object 目标数据\n' +
          '*/\n' +
          'function transform(data) {\n' +
          '    return data;\n' +
          '}',
        paramContent: '{}',
        resultContent: ''
      },
      url: {
        info: '/api/script/info',
        add: '/api/script/add',
        update: '/api/script/update',
        run: '/api/script/run'
      },
      rules: {
        scriptName: [{ required: true, message: '请输入脚本名称', trigger: 'blur' }],
        scriptContent: [{ required: true, message: '请输入脚本内容', trigger: 'blur' }]
      },
      options: {
        mode: { name: 'text/javascript', json: true },
        height: 450,
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
      },
      disSaveBtn: true,
      time: -1
    }
  },
  mounted() {
    let scriptId = this.$route.params.scriptId
    if (scriptId !== 'new') {
      this.scriptId = scriptId
      getAction(this.url.info, { scriptId: this.scriptId }).then(res => {
        this.modal = res.data
      })
    }
  },
  methods: {
    formatParam(){
      this.modal.paramContent = formatJson(this.modal.paramContent)
    },
    formatResult(){
      this.modal.resultContent = formatJson(this.modal.resultContent)
    },
    runScript() {
      postAction(this.url.run, this.modal).then(res => {
        if (res.code === 200) {
          this.modal.resultContent = JSON.stringify(res.data.result)
          this.time = res.data.time
          this.$message.success('运行成功')
        } else {
          this.$message.error('运行失败: ' + res.message)
        }
      })
    },
    contentChange() {
      this.disSaveBtn = false
    },
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
              that.$message.success('保存成功')
              that.disSaveBtn = true
              that.modal = res.data
            } else {
              that.$message.warning('保存失败')
            }
          })
            .finally(() => {
              that.confirmLoading = false
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

.scriptModel .CodeMirror {
  height: 470px;
}

.scriptModel .CodeMirror-scroll {
  height: 470px;
}

.ant-card-extra {
  padding: 0;
}

.inputModel .CodeMirror {
  height: 250px;
}

.inputModel .CodeMirror-scroll {
  height: 250px;
}

.outputModel .CodeMirror {
  height: 250px;
}

.outputModel .CodeMirror-scroll {
  height: 250px;
}

</style>
