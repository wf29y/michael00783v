<template>
  <a-form-model layout='vertical' :model='properties'>
    <a-form-model-item label='Topic'>
      <a-input v-model='properties.topic' placeholder='请输入Topic' />
    </a-form-model-item>
    <a-form-model-item label='Qos'>
      <a-input-number style='width: 100%' v-model='properties.qos' placeholder='请输入Qos' />
    </a-form-model-item>
  </a-form-model>

</template>

<script>

export default {
  data() {
    return {
      properties: {}
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
