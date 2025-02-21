<template>
  <div>
    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>

      <a-card :bordered='false' style='margin-bottom: 20px' :body-style='{padding:"17px 24px"}'>
        <a-row>
          <a-col :span='12' style='font-size: 16px;font-weight: bold;color:rgba(0, 0, 0, 0.85);padding-top: 4px'>
            {{ modal.ruleId ? '编辑' : '新建' }}规则
          </a-col>
          <a-col :span='12' style='text-align: right'>
            <a-button @click='onClose' style='width:90px;margin-right: 8px'> 取消</a-button>
            <a-button @click='saveRule' style='width:90px' type='primary'> 保存</a-button>
          </a-col>
        </a-row>
      </a-card>

      <a-card :body-style='{paddingBottom:0}' style='margin-bottom: 20px' :bordered='false'>
        <a-row :gutter='20'>
          <a-col :span='12'>
            <a-form-model-item label='名称' prop='ruleName'>
              <a-input v-model='modal.ruleName' placeholder='请输入规则名称' />
            </a-form-model-item>
          </a-col>
          <a-col :span='12'>
            <a-form-model-item label='备注' prop='description'>
              <a-input v-model='modal.description' placeholder='请输入备注' />
            </a-form-model-item>
          </a-col>
          <a-col :span='12'>
            <a-form-model-item label='解析方式' prop='analysisMode'>
              <a-select v-model='modal.analysisMode' placeholder='请选择解析方式' @change='analysisModeChange'>
                <a-select-option v-for='(item,index) in analysisModeList' :value='item.value' :key='index'>
                  {{ item.name }}
                </a-select-option>
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
        </a-row>
      </a-card>

      <a-card style='margin-bottom: 20px' :bordered='false'>
        <div class='title'>数据源</div>
        <a-row style='background-color: #f6f6f6;padding: 15px 10px 0 15px' v-if='modal.sourceResource'>
          <a-col :span='18'>
            <a-descriptions :column='2'>
              <a-descriptions-item label='资源名称'>
                {{ modal.sourceResource.resourceName }}
              </a-descriptions-item>
              <a-descriptions-item label='资源类型'>
                {{ resourceTypeMap[modal.sourceResource.resourceType] }}
              </a-descriptions-item>
              <a-descriptions-item v-for='(element,index) in getDetails(modal.sourceResource)' :key='index'
                                   :label='element.name'>
                {{ element.value }}
              </a-descriptions-item>
            </a-descriptions>
          </a-col>
          <a-col :span='6' style='text-align: right'>
            <a @click='editResource("source",modal.sourceResource)'>配置</a>
            <a-divider type='vertical' />
            <a-popconfirm title='移除此资源?' @confirm='() => deleteResource("source")'>
              <a href='javascript:;'>移除</a>
            </a-popconfirm>
          </a-col>
        </a-row>
        <a-button @click='addResource("source")' v-if='!modal.sourceResource'> 绑定数据源</a-button>
      </a-card>


      <a-card style='margin-bottom: 20px' :bordered='false'>
        <div class='title'>目标资源</div>
        <a-list :grid="{ gutter: 16, xs: 1, sm: 1, md: 1, lg: 1, xl: 1, xxl: 1 }" :data-source='modal.destResourceList' v-if='modal.destResourceList.length>0'>
          <a-list-item slot='renderItem' slot-scope='resource,index'>
            <a-row style='background-color: #f6f6f6;padding: 15px 10px 0 15px'>
              <a-col :span='18'>
                <a-descriptions :column='2'>
                  <a-descriptions-item label='资源名称'>
                    {{ resource.resourceName }}
                  </a-descriptions-item>
                  <a-descriptions-item label='资源类型'>
                    {{ resourceTypeMap[resource.resourceType] }}
                  </a-descriptions-item>
                  <a-descriptions-item v-for='(element,index) in getDetails(resource)' :key='index'
                                       :label='element.name'>
                    {{ element.value }}
                  </a-descriptions-item>
                </a-descriptions>
              </a-col>
              <a-col :span='6' style='text-align: right'>
                <a @click='editResource("dest",resource,index)'>配置</a>
                <a-divider type='vertical' />
                <a-popconfirm title='移除此资源?' @confirm='() => deleteResource("dest",index)'>
                  <a href='javascript:;'>移除</a>
                </a-popconfirm>
              </a-col>
            </a-row>

          </a-list-item>
        </a-list>
        <a-button @click='addResource("dest")'> 添加目标资源</a-button>
      </a-card>


      <a-card style='margin-bottom: 20px' :bordered='false' v-if="modal.analysisMode!=='WITHOUT'">

        <a-row style='padding: 0'>
          <a-col :span='12' class='title'>数据解析</a-col>
          <a-col :span='12' style='text-align: right' v-if="modal.analysisMode==='SCRIPT'"><a
            @click='selectScript'>选择脚本</a></a-col>
        </a-row>

        <a-row :gutter='20'>
          <a-col :span='24' class='ruleModel' v-if="modal.analysisMode==='SCRIPT'">
            <codemirror v-model='modal.script' :options='options' style='border:  1px #e8e3e3 solid'></codemirror>
          </a-col>
          <a-col :span='24'>
            <a-form-model-item label='Jar包地址' prop='script' v-if="modal.analysisMode==='PLUGIN'">
              <a-input placeholder='请输入Jar包地址' />
            </a-form-model-item>
          </a-col>
          <a-col :span='24'>
            <a-form-model-item label='解析类名' prop='script' v-if="modal.analysisMode==='PLUGIN'">
              <a-input placeholder='请输入解析类名' />
            </a-form-model-item>
          </a-col>
        </a-row>

      </a-card>

      <variables-model ref='VariablesModel'></variables-model>

    </a-form-model>
    <resource-model ref='ResourceModel' @update='handleUpdateResource' @add='handleAddResource'></resource-model>
    <script-select-model ref='ScriptSelectModel' @select='handleSelectedScript'></script-select-model>


  </div>
</template>

<script>
import { postAction, putAction, getAction } from '@/api/manage'
import ResourceModel from './modules/ResourceModel'
import ScriptSelectModel from './modules/ScriptSelectModel'
import VariablesModel from './modules/VariablesModel'
import { codemirror } from 'vue-codemirror-lite'
import { resourceTypeMap, getResourceDetails } from '@/config/resource.config'
import { analysisModeList } from '@/config/rule.config'

require('codemirror/mode/javascript/javascript')
require('codemirror/mode/vue/vue')
require('codemirror/addon/hint/show-hint.js')
require('codemirror/addon/hint/show-hint.css')
require('codemirror/addon/hint/javascript-hint.js')
require('codemirror/theme/base16-light.css')
require('codemirror/addon/selection/active-line')


export default {
  components: { ResourceModel, ScriptSelectModel, VariablesModel, codemirror },
  data() {
    return {
      modal: {
        sourceResource: null,
        destResourceList: [],
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
      resourceTypeMap: resourceTypeMap,
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
        extraKeys: { 'tab': 'autocomplete' }, //自定义快捷键
        hintOptions: {
          tables: {}
        }
      },
      analysisModeList: analysisModeList,
      defaultScript: '/**\n' +
        '* 方法名transform不可修改,入参：data Object 源数据,出参：data Object 目标数据\n' +
        '*/\n' +
        'function transform(data) {\n' +
        '    return data;\n' +
        '}'
    }
  },
  mounted() {
    let ruleId = this.$route.params.ruleId
    if (ruleId !== 'new') {
      this.ruleId = ruleId
      getAction(this.url.info, { ruleId: this.ruleId }).then(res => {
        let temp = res.data
        if (temp) {
          this.modal = res.data
          // this.modal.destResourceList.push({})
          this.$nextTick(() => {
            this.$refs.VariablesModel.set(this.modal.variables)
          })
        }
      })
    }
  },
  methods: {
    getDetails(resource) {
      return getResourceDetails(resource, 'rule')
    },
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
        this.modal.sourceResource = null
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
        this.$set(this.modal.destResourceList, index, resource)
        //this.modal.destResourceList[index] = resource
      } else if (mode === 'source') {
        this.modal.sourceResource = resource
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
          rule.variables = this.$refs.VariablesModel.get()
          let obj
          if (this.ruleId) {
            obj = putAction(this.url.update, rule)
          } else {
            obj = postAction(this.url.add, rule)
          }
          obj.then(res => {
            if (res.code === 200) {
              that.$message.success('保存成功')
              that.onClose()
            } else {
              that.$message.error('保存失败')
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
    selectScript() {
      this.$refs.ScriptSelectModel.show()
    },
    handleSelectedScript(script) {
      this.modal.script = script.scriptContent
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
  height: 195px;
}

.ruleModel .CodeMirror-scroll {
  height: 195px;
}


.title {
  font-size: 16px;
  font-weight: bold;
  color: rgba(0, 0, 0, 0.85);
  margin-bottom: 16px;
}

</style>



