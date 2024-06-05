<template>
  <a-form-model layout="vertical" :model="properties">
    <a-row :gutter='16'>
      <a-col :span='24'>
        <a-form-model-item label='URL'>
          <a-input v-model='properties.url' placeholder='请输入URL' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='账户'>
          <a-input v-model='properties.username' placeholder='请输入账户' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='密码'>
          <a-input-password v-model='properties.password' placeholder='请输入密码' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='连接超时'>
          <a-input v-model='properties.connectionTimeout' placeholder='请输入连接超时' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='保活间隔'>
          <a-input v-model='properties.keepAliveInterval' placeholder='请输入保活间隔' />
        </a-form-model-item>
      </a-col>
    </a-row>
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
      this.properties = properties
    },
    get() {
      return this.properties
    }
  }
}
</script>

<style scoped>
</style>
