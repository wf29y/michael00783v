<template>
  <div>
    <a-card title='规则'>
      <div slot='extra'>
        <a-button :style="{ marginRight: '8px' }" @click='onClose' icon='close'> 取消</a-button>
        <a-button type='primary' @click='saveRule' icon='save'> 保存</a-button>
      </div>
      <a-row :gutter='20'>
        <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>
          <a-col :span='12'>
            <a-form-model-item label='规则名称' prop='ruleName'>
              <a-input v-model='modal.ruleName' placeholder='请输入规则名称' />
            </a-form-model-item>
          </a-col>
          <a-col :span='12'>
            <a-form-model-item label='备注' prop='description'>
              <a-input v-model='modal.description' placeholder='请输入备注' />
            </a-form-model-item>
          </a-col>
        </a-form-model>
      </a-row>
    </a-card>

    <a-row :gutter='20' style='margin-top: 20px'>

      <a-col :span='12'>
        <a-card title='数据源' :body-style='{minHeight:"600px"}'>
          <a-row>
            <a-col :span='12'>
              <template v-if='!modal.sourceResource || modal.sourceResource.resourceId === undefined'>
                <a-button @click='addSourceResource()' class='new-btn' type='dashed' style='height: 194px;width: 100%'>
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
                  <a slot='actions' @click='editSourceResource()'>配置</a>
                  <a-popconfirm slot='actions' title='移除此资源?' @confirm='() => deleteSourceResource()'>
                    <a href='javascript:;'>移除</a>
                  </a-popconfirm>
                </a-card>
              </template>
            </a-col>
          </a-row>
          <div style='padding: 20px 0 5px 0'>解析脚本:</div>
          <a-textarea v-model='modal.script' :rows='7' placeholder='请输入解析脚本' />
        </a-card>
      </a-col>
      <a-col :span='12'>
        <a-card title='目标' :body-style='{minHeight:"600px"}'>
          <a-list :grid='{ gutter: 24, lg: 2, md: 2, sm: 2, xs: 2 }' :data-source='modal.destResourceList'>
            <a-list-item slot='renderItem' slot-scope='item,index'>
              <template v-if='!item || item.resourceId === undefined'>
                <a-button @click='addDestResource()' class='new-btn' type='dashed' style='height: 194px;width: 100%'>
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
                  <a slot='actions' @click='editDestResource(item,index)'>配置</a>
                  <a-popconfirm slot='actions' title='移除此资源?' @confirm='() => deleteDestResource(index)'>
                    <a href='javascript:;'>移除</a>
                  </a-popconfirm>
                </a-card>
              </template>
            </a-list-item>
          </a-list>
        </a-card>
      </a-col>

    </a-row>

    <dest-resource-model ref='DestResourceModel' @update='handleUpdateDestResource'
                         @add='handleAddDestResource'></dest-resource-model>

    <source-resource-model ref='SourceResourceModel' @update='handleUpdateSourceResource'></source-resource-model>

  </div>
</template>

<script>
import { postAction, putAction, getAction } from '@/api/manage'
import DestResourceModel from './DestResourceModel'
import SourceResourceModel from './SourceResourceModel'

export default {
  components: { DestResourceModel, SourceResourceModel },
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
        destResourceList: [{}]
      },
      url: {
        add: '/api/rule/add',
        update: '/api/rule/update',
        info: '/api/rule/info'
      },
      rules: {
        ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }]
      },
      resourceTypeMap: { MQTT: 'MQTT Broker' }
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
    // 元数据操作
    addSourceResource() {
      this.$refs.SourceResourceModel.add()
    },
    editSourceResource() {
      this.$refs.SourceResourceModel.edit(this.modal.sourceResource)
    },
    deleteSourceResource() {
      this.modal.sourceResource = {}
    },
    handleUpdateSourceResource(resource) {
      this.modal.sourceResource = resource
    },


    // 目的资源操作
    addDestResource() {
      this.$refs.DestResourceModel.add()
    },
    editDestResource(resource, index) {
      this.$refs.DestResourceModel.edit(resource, index)
    },
    deleteDestResource(index) {
      this.modal.destResourceList.splice(index, 1)
    },
    handleAddDestResource(resource) {
      this.modal.destResourceList.push(resource)
    },
    handleUpdateDestResource(resource, index) {
      this.modal.destResourceList[index] = resource
    },

    // 保存规则
    saveRule() {
      const that = this
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          that.confirmLoading = true
          let rule = JSON.parse(JSON.stringify(this.modal))
          rule.destResourceList.splice(0,1);
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

    onClose() {
      this.$router.push({ name: 'ruleList' })
    }


  }
}
</script>

<style scoped>
</style>
