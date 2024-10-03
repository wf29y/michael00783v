<template>
  <a-form-model layout='vertical' :model='properties'>
    <a-row :gutter='16'>
      <a-col :span='12'>
        <a-form-model-item label='IP'>
          <a-input v-model='properties.ip' placeholder='请输入IP' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='端口'>
          <a-input v-model='properties.port' placeholder='请输入端口' />
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
        <a-form-model-item label='数据库名称'>
          <a-input v-model='properties.databaseName' placeholder='请输入数据库名称' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='连接数初始大小'>
          <a-input v-model='properties.initSize' placeholder='请输入连接数初始大小' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='连接数下限'>
          <a-input v-model='properties.minIdle' placeholder='请输入连接池下限' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='连接数上限'>
          <a-input v-model='properties.maxActive' placeholder='请输入连接数上限' />
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
        port: 3306,
        initSize: 8,
        minIdle: 1,
        maxActive: 20
      }
    }
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
    },
    get() {
      return this.properties
    }
  }
}
</script>

<style scoped>
</style>
