<template>
  <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
    <a-row :gutter='24'>
      <a-col :span='24'>
        <a-form-model-item label='端口' prop='port'>
          <a-input v-model='properties.port' placeholder='请输入端口' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='主线程数' prop='bossTread'>
          <a-input-number v-model='properties.bossTread' placeholder='请输入主线程数' style='width: 100%'/>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='工作线程数' prop='workerTread'>
          <a-input-number v-model='properties.workerTread' placeholder='请输入工作线程数' style='width: 100%'/>
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
        bossTread: 1,
        workerTread: 5
      },
      rules: {
        port: [{ required: true, message: '请输入端口', trigger: 'blur' }]
      }
    }
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
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
