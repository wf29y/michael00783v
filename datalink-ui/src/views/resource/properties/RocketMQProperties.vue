<template>
  <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
    <a-row :gutter='24'>
      <a-col :span='12'>
        <a-form-model-item label='IP' prop='ip'>
          <a-input v-model='properties.ip' placeholder='请输入IP' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='端口' prop='port'>
          <a-input-number v-model='properties.port' placeholder='请输入端口' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-form-model-item label='分组(group)' prop='group'>
          <a-input v-model='properties.group' placeholder='请输入分组' />
        </a-form-model-item>
      </a-col>
    </a-row>
  </a-form-model>

</template>

<script>

export default {
  data() {
    return {
      properties: {
        port: 9876,
      },
      rules: {
        ip: [{ required: true, message: '请输入IP', trigger: 'blur' }],
        port: [{ required: true, message: '请输入端口', trigger: 'blur' }],
        group: [{ required: true, message: '请输入分组', trigger: 'blur' }]
      }
    }
  },
  methods: {
    set(properties) {
      this.properties = properties
    },
    get(callback) {
      let that = this
      this.$refs.propForm.validate(valid => {
        if (valid) {
          return callback(true, that.properties)
        } else {
          return callback(false)
        }
      })
    }
  }
}
</script>

<style scoped>
</style>
