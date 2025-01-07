<template>
  <a-form-model layout="vertical" :model="properties">
    <a-row :gutter='20'>
      <a-col :span='24'>
        <a-form-model-item label='URL'>
          <a-input v-model='properties.url' placeholder='请输入URL' />
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
