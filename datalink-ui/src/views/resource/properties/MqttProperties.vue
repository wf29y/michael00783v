<template>
  <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
    <a-row :gutter='24'>
      <a-col :span='24'>
        <a-form-model-item label='URL' prop='url'>
          <a-input v-model='properties.url' placeholder='请输入URL [ tcp://ip:port 或 ssl://ip:port ]' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='MQTT版本' prop='version'>
          <a-select v-model='properties.version' placeholder='请选择MQTT版本'>
            <a-select-option :value='3'>3.1</a-select-option>
            <a-select-option :value='4'>3.1.1</a-select-option>
            <a-select-option :value='5'>5.0</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='SSL/TLS' prop='ssl'>
          <a-select v-model='properties.ssl' placeholder='请选择是否启用SSL'>
            <a-select-option :value='true'>是</a-select-option>
            <a-select-option :value='false'>否</a-select-option>
          </a-select>
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
      properties: {
        url: 'tcp://127.0.0.1:1883',
        ssl: false,
        version: 3,
        connectionTimeout: 10,
        keepAliveInterval: 30
      },
      rules: {
        url: [{ required: true, message: '请输入URL', trigger: 'blur' }],
        ssl: [{ required: true, message: '请选择是否启用SSL', trigger: 'change' }],
        version: [{ required: true, message: '请选择MQTT版本', trigger: 'change' }],
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
