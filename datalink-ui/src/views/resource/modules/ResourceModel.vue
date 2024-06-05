<template>
  <a-drawer
    :confirmLoading='confirmLoading'
    title='资源'
    :width='720'
    :visible='visible'
    :body-style="{ paddingBottom: '80px' }"
    @close='onClose'
  >
    <a-form-model ref='ruleForm' :model='modal' layout='vertical'  :rules="rules">
      <a-row :gutter='16'>
        <a-col :span='12'>
          <a-form-model-item label='资源类型' prop='resourceType'>
            <a-select v-model='modal.resourceType' placeholder='请选择资源类型'>
              <a-select-option value='MQTT'> MQTT Broker</a-select-option>
            </a-select>
          </a-form-model-item>
        </a-col>
        <a-col :span='12'>
          <a-form-model-item label='资源名称' prop='resourceName'>
            <a-input v-model='modal.resourceName' placeholder='请输入资源名称' />
          </a-form-model-item>
        </a-col>
      </a-row>
      <mqtt-properties v-if="modal.resourceType === 'MQTT'" ref='PropertiesModal'></mqtt-properties>
      <a-form-model-item label='备注' prop='description'>
        <a-textarea v-model='modal.description' :rows='4' placeholder='请输入备注'
        />
      </a-form-model-item>
    </a-form-model>
    <div
      :style="{
        position: 'absolute',
        right: 0,
        bottom: 0,
        width: '100%',
        borderTop: '1px solid #e9e9e9',
        padding: '10px 16px',
        background: '#fff',
        textAlign: 'right',
        zIndex: 1
      }"
    >
      <a-button :style="{ marginRight: '8px' }" @click='onClose'> Cancel</a-button>
      <a-button type='primary' @click='handleOk'> Submit</a-button>
    </div>
  </a-drawer>
</template>

<script>
import { postAction, putAction } from '@/api/manage'
import MqttProperties from '../properties/MqttProperties'

export default {
  name:'ResourceModel',
  components: { MqttProperties },
  data() {
    return {
      title: '操作',
      visible: false,
      confirmLoading: false,
      modal: {},
      url: {
        add: '/api/resource/add',
        update: '/api/resource/update'
      },
      rules: {
        resourceType: [{ required: true, message: '请选择资源类型', trigger: 'blur' }],
        resourceName: [{ required: true, message: '请输入资源名称', trigger: 'blur' }]
      },
    }
  },
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      this.modal = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        if(this.modal.resourceType){
          this.$refs.PropertiesModal.set(this.modal.properties)
        }
      })
    },
    onClose() {
      this.visible = false
    },
    handleOk() {
      const that = this
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          that.confirmLoading = true
          this.modal.properties = this.$refs.PropertiesModal.get()
          let obj
          if (this.modal.resourceId) {
            obj = putAction(this.url.update, this.modal)
          } else {
            obj = postAction(this.url.add, this.modal)
          }
          obj.then(res => {
            if (res.code === 200) {
              that.$message.success(res.message)
              that.$emit('ok')
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
    filterOption(input, option) {
      if (!option.componentOptions.children[0].text) {
        return false
      }
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },
    handleCancel() {
      this.onClose()
    }
  }
}
</script>

<style scoped>
</style>
