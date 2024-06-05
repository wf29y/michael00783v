<template>
  <a-modal
    :confirmLoading='confirmLoading'
    title='源数据'
    :width='500'
    :visible='visible'
    @cancel='onClose'
  >
    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>
      <a-form-model-item label='资源' prop='resourceName'>
        <a-select v-model='modal.resourceId' placeholder='请选择资源' @change='resourceChange'>
          <a-select-option v-for='(item,index) in resourceList' :value='item.resourceId' :key='index'>
            {{ item.resourceName }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <mqtt-properties v-if="modal.resourceType === 'MQTT'" ref='PropertiesModal'></mqtt-properties>
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
      <a-button :style="{ marginRight: '8px' }" @click='onClose'> 取消</a-button>
      <a-button type='primary' @click='handleOk'> 确定</a-button>
    </div>
  </a-modal>
</template>

<script>
import { postAction } from '@/api/manage'
import MqttProperties from '../properties/MqttProperties'

export default {
  name: 'DestResourceModel',
  components: { MqttProperties },
  data() {
    return {
      title: '操作',
      visible: false,
      confirmLoading: false,
      modal: {},
      url: {
        resourceList: '/api/resource/list'
      },
      rules: {
        resourceId: [{ required: true, message: '请选择资源', trigger: 'blur' }]
      },
      resourceList: [],
    }
  },
  mounted() {
  },
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      this.listResource()
      this.modal =  JSON.parse(JSON.stringify(record))
      this.visible = true
      this.$nextTick(() => {
        if (this.modal.resourceType) {
          this.$refs.PropertiesModal.set(this.modal.properties)
        }
      })
    },
    listResource() {
      postAction(this.url.resourceList, {}).then(res => {
        this.resourceList = res.data
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
          that.modal.properties = that.$refs.PropertiesModal.get()
          that.$emit('update', this.modal)
          that.confirmLoading = false
          that.visible = false
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
    },
    resourceChange(resourceId) {
      let index = this.resourceList.findIndex(resource => resource.resourceId === resourceId)
      this.modal = this.resourceList[index]
      this.$nextTick(() => {
        if (this.modal.resourceType) {
          this.$refs.PropertiesModal.set(this.modal.properties)
        }
      })
    }
  }
}
</script>

<style scoped>
</style>
