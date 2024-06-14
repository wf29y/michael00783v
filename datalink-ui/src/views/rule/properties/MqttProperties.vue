<template>
  <a-row :gutter='20'>
    <a-form-model layout='vertical' :model='properties'>
      <a-col :span='24'>
        <a-form-model-item label='Topic'>
          <a-input v-model='properties.topic' placeholder='请输入Topic' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-form-model-item label='Qos'>
          <a-input-number v-model='properties.qos' placeholder='请输入Qos' style='width: 100%'/>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='动态Topic' prop='dynamicTopic' v-show="type==='dest'">
          <a-select v-model='properties.dynamicTopic' placeholder='请选择是否动态Topic'>
            <a-select-option :value='true'>是</a-select-option>
            <a-select-option :value='false'>否</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='消息保留' prop='retained' v-show="type==='dest'">
          <a-select v-model='properties.retained' placeholder='请选择是否消息保留'>
            <a-select-option :value='true'>是</a-select-option>
            <a-select-option :value='false'>否</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-form-model-item label='消息模板' v-show="type==='dest'" style='margin-bottom: 0'>
          <a-textarea :rows='6' v-model='properties.template' placeholder='消息模板' />
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>

export default {
  data() {
    return {
      properties: {}
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

<style scoped>
</style>
