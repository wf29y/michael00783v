<template>
  <div>
    <a-card title='规则' :body-style='{paddingBottom:0}'>
      <div slot='extra'>
        <a-button :style="{ marginRight: '8px' }" @click='onClose' icon='close'> 取消</a-button>
        <a-button type='primary' @click='saveRule' icon='save'> 保存</a-button>
      </div>
      <a-row :gutter='20'>
        <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>
          <a-col :span='8' v-if='modal.ruleId'>
            <a-form-model-item label='ID' prop='ruleId'>
              <a-input v-model='modal.ruleId' placeholder='请输入规则ID' :disabled='true' />
            </a-form-model-item>
          </a-col>
          <a-col :span='modal.ruleId?8:12'>
            <a-form-model-item label='名称' prop='ruleName'>
              <a-input v-model='modal.ruleName' placeholder='请输入规则名称' />
            </a-form-model-item>
          </a-col>
          <a-col :span='modal.ruleId?8:12'>
            <a-form-model-item label='备注' prop='description'>
              <a-input v-model='modal.description' placeholder='请输入备注' />
            </a-form-model-item>
          </a-col>
        </a-form-model>
      </a-row>
    </a-card>

    <a-row :gutter='20' style='margin-top: 20px'>

      <a-col :span='12'>
        <a-card title='数据源' :body-style='{minHeight:"600px",paddingBottom:0}'>
          <a-row :gutter='20' style='margin-bottom: 20px'>
            <a-col :span='12'>
              <template v-if='!modal.sourceResource || modal.sourceResource.resourceId === undefined'>
                <a-button @click='addResource("source")' class='new-btn' type='dashed' style='height: 194px;width: 100%'>
                  <a-icon type='plus' />
                  绑定数据源
                </a-button>
              </template>
              <template v-else>
                <a-card>
                  <div slot='title'>{{ modal.sourceResource.resourceName }}</div>
                  <a-row :gutter='16'>
                    <a-col :span='8'>
                      <div>资源ID：</div>
                    </a-col>
                    <a-col :span='16'>
                      <div>{{ modal.sourceResource.resourceId }}</div>
                    </a-col>
                  </a-row>
                  <a-row :gutter='16'>
                    <a-col :span='8'>
                      <div>协议类型：</div>
                    </a-col>
                    <a-col :span='16'>
                      <div>{{ resourceTypeMap[modal.sourceResource.resourceType] }}</div>
                    </a-col>
                  </a-row>
                  <a slot='actions' @click='editResource("source",modal.sourceResource)'>配置</a>
                  <a-popconfirm slot='actions' title='移除此资源?' @confirm='() => deleteResource("source")'>
                    <a href='javascript:;'>移除</a>
                  </a-popconfirm>
                </a-card>
              </template>
            </a-col>
            <a-col :span='12'>
              <a-card style='height: 194px;' title='data' size='small' :body-style='{padding:"10px 20px"}'
                      v-show='modal.sourceResource.resourceId'>
                <pre>{{ dataFormatMap[modal.sourceResource.resourceType] }}</pre>
              </a-card>
            </a-col>
          </a-row>
          <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>
            <a-row :gutter='20'>
              <a-col :span='12'>
                <a-form-model-item label='解析方式' prop='analysisMode'>
                  <a-select v-model='modal.analysisMode' placeholder='请选择解析方式' @change='analysisModeChange'>
                    <a-select-option value='WITHOUT'>无解析透传</a-select-option>
                    <a-select-option value='SCRIPT'>JavaScript脚本</a-select-option>
                    <a-select-option value='JAR'>Jar包</a-select-option>
                  </a-select>
                </a-form-model-item>
              </a-col>
              <a-col :span='12'>
                <a-form-model-item label='忽略空值' prop='ignoreNullValue'>
                  <a-select v-model='modal.ignoreNullValue' placeholder='请选择是否忽略空值'>
                    <a-select-option :value='true'>是</a-select-option>
                    <a-select-option :value='false'>否</a-select-option>
                  </a-select>
                </a-form-model-item>
              </a-col>
              <a-col :span='24' class='ruleModel'>
                <a-form-model-item label='解析脚本' prop='script' v-if="modal.analysisMode==='SCRIPT'">
                  <div style='margin-top: -30px;width: 100%;text-align: right;height: 30px;padding-top: 5px'>
                    <a @click='selectScript'>选择脚本</a>
                  </div>
                  <codemirror v-model='modal.script' :options='options' style='border:  1px #e8e3e3 solid'></codemirror>
                </a-form-model-item>
              </a-col>
              <a-col :span='24'>
                <a-form-model-item label='Jar包地址' prop='script' v-show="modal.analysisMode==='JAR'">
                  <a-input placeholder='请输入Jar包地址' />
                </a-form-model-item>
              </a-col>
              <a-col :span='24'>
                <a-form-model-item label='解析类名' prop='script' v-show="modal.analysisMode==='JAR'">
                  <a-input placeholder='请输入解析类名' />
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-form-model>
        </a-card>
      </a-col>
      <a-col :span='12'>
        <a-card title='目标' :body-style='{minHeight:"600px"}'>
          <a-list :grid='{ gutter: 24, lg: 2, md: 2, sm: 2, xs: 2 }' :data-source='modal.destResourceList'>
            <a-list-item slot='renderItem' slot-scope='item,index'>
              <template v-if='!item || item.resourceId === undefined'>
                <a-button @click='addResource("dest")' class='new-btn' type='dashed' style='height: 194px;width: 100%'>
                  <a-icon type='plus' />
                  添加目标资源
                </a-button>
              </template>
              <template v-else>
                <a-card>
                  <div slot='title'>{{ item.resourceName }}</div>
                  <a-row :gutter='16'>
                    <a-col :span='8'>
                      <div>资源ID：</div>
                    </a-col>
                    <a-col :span='16'>
                      <div>{{ item.resourceId }}</div>
                    </a-col>
                  </a-row>
                  <a-row :gutter='16'>
                    <a-col :span='8'>
                      <div>协议类型：</div>
                    </a-col>
                    <a-col :span='16'>
                      <div>{{ resourceTypeMap[item.resourceType] }}</div>
                    </a-col>
                  </a-row>
                  <a slot='actions' @click='editResource("dest",item,index)'>配置</a>
                  <a-popconfirm slot='actions' title='移除此资源?' @confirm='() => deleteResource("dest",index)'>
                    <a href='javascript:;'>移除</a>
                  </a-popconfirm>
                </a-card>
              </template>
            </a-list-item>
          </a-list>
        </a-card>
      </a-col>

    </a-row>

    <resource-model ref='ResourceModel' @update='handleUpdateResource' @add='handleAddResource'></resource-model>
    <script-select-model ref='ScriptSelectModel' @select='handleSelectedScript'></script-select-model>
  </div>
</template>

<script>
import { postAction, putAction, getAction } from '@/api/manage'
import ResourceModel from './modules/ResourceModel'
import ScriptSelectModel from './modules/ScriptSelectModel'
import { codemirror } from 'vue-codemirror-lite'

require('codemirror/mode/javascript/javascript')
require('codemirror/mode/vue/vue')
require('codemirror/addon/hint/show-hint.js')
require('codemirror/addon/hint/show-hint.css')
require('codemirror/addon/hint/javascript-hint.js')
require('codemirror/theme/base16-light.css')
require('codemirror/addon/selection/active-line')


export default {
  components: { ResourceModel,ScriptSelectModel, codemirror },
  props: {
    ruleId: {
      type: String,
      default: undefined
    }
  },
  data() {
    return {
      modal: {
        sourceResource: {},
        destResourceList: [{}],
        analysisMode: 'WITHOUT',
        ignoreNullValue: false
      },
      url: {
        add: '/api/rule/add',
        update: '/api/rule/update',
        info: '/api/rule/info'
      },
      rules: {
        ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
        analysisMode: [{ required: true, message: '请选择解析方式', trigger: 'blur' }],
        ignoreNullValue: [{ required: true, message: '请选择是否忽略空值', trigger: 'blur' }]
      },
      resourceTypeMap: { MQTT: 'MQTT Broker' },
      options: {
        mode: { name: 'text/javascript', json: true },
        height: 150,
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
      },
      defaultScript:
        '/**\n' +
        ' * 方法名transform不可修改,入参：data Object 源数据,出参：data Object 目标数据\n' +
        ' */\n' +
        'function transform(data) {\n' +
        '    return data;\n' +
        '}',
      dataFormatMap: {
        MQTT:
          '{\n' +
          '  "topic":String,\n' +
          '  "payload":String\n' +
          '}'
      }
    }
  },
  mounted() {
    if (this.ruleId) {
      getAction(this.url.info, { ruleId: this.ruleId }).then(res => {
        let temp = res.data
        if (temp) {
          this.modal = res.data
          this.modal.destResourceList.unshift({})
        }
      })
    }
  },
  methods: {
    // 操作
    addResource(mode) {
      this.$refs.ResourceModel.add(mode)
    },
    editResource(mode, resource, index) {
      this.$refs.ResourceModel.edit(mode, resource, index)
    },
    deleteResource(mode, index) {
      if (mode === 'dest') {
        this.modal.destResourceList.splice(index, 1)
      } else if (mode === 'source') {
        this.modal.sourceResource = {}
      }
    },

    handleAddResource(mode, resource) {
      if (mode === 'dest') {
        this.modal.destResourceList.push(resource)
      } else if (mode === 'source') {
        this.modal.sourceResource = resource
      }
    },
    handleUpdateResource(mode, resource, index) {
      if (mode === 'dest') {
        this.modal.destResourceList[index] = resource
      } else if (mode === 'source') {
        this.modal.sourceResource = {}
      }
    },

    analysisModeChange(mode) {
      if (mode === 'SCRIPT' && !this.modal.script) {
        this.modal.script = this.defaultScript
      } else {
        this.modal.script = ''
      }
    },

    // 保存规则
    saveRule() {
      const that = this
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          that.confirmLoading = true
          let rule = JSON.parse(JSON.stringify(this.modal))
          rule.destResourceList.splice(0, 1)
          let obj
          if (this.ruleId) {
            obj = putAction(this.url.update, rule)
          } else {
            obj = postAction(this.url.add, rule)
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
    selectScript(){
      this.$refs.ScriptSelectModel.show();
    },
    handleSelectedScript(script){
      this.modal.script = script.scriptContent;
    },
    onClose() {
      this.$router.push({ name: 'ruleList' })
    }


  }
}
</script>

<style>

.ruleModel .cm-s-base16-light.CodeMirror {
  background: white !important;
  color: #202020;
}

.ruleModel .cm-s-base16-light span.cm-comment {
  font-size: 13px;
}

.ruleModel .cm-s-base16-light .CodeMirror-activeline-background {
  background: #f3f2f2;
}

.ruleModel .CodeMirror {
  height: 200px;
}

.ruleModel .CodeMirror-scroll {
  height: 200px;
}

</style>
