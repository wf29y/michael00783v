<template>
  <a-row :gutter='20'>
    <a-form-model layout='vertical' :model='properties'>
      <a-col :span='24'>
        <a-form-model-item label='SQL模板' v-show="type==='dest'" style='margin-bottom: 0'>
          <a-textarea :rows='6' v-model='properties.sql' placeholder='SQL模板' />
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
