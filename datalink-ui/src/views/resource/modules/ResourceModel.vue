<template>
  <a-drawer
    :confirmLoading='confirmLoading'
    title='资源'
    :width='720'
    :visible='visible'
    :destroyOnClose='true'
    :body-style="{ paddingBottom: '80px' }"
    @close='onClose'
  >
    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>
      <a-row :gutter='24'>
        <a-col :span='12'>
          <a-form-model-item label='资源类型' prop='resourceType'>
            <a-select v-model='modal.resourceType' placeholder='请选择资源类型' :disabled='modal.resourceId' show-search>
              <a-select-opt-group v-for='(group,groupIndex) in resourceTypeAllList' :key='groupIndex' :label='group.group'>
                <a-select-option v-for='(item,itemIndex) in group.list' :value='item.code' :key='itemIndex'>{{ item.name
                  }}
                </a-select-option>
              </a-select-opt-group>
            </a-select>
          </a-form-model-item>
        </a-col>
        <a-col :span='12'>
          <a-form-model-item label='资源名称' prop='resourceName'>
            <a-input v-model='modal.resourceName' placeholder='请输入资源名称' />
          </a-form-model-item>
        </a-col>
      </a-row>
      <mqtt-properties v-if="modal.resourceType === 'MQTT'" ref='PropertiesModal'></mqtt-properties>
      <kafka-properties v-if="modal.resourceType === 'KAFKA'" ref='PropertiesModal'></kafka-properties>
      <mysql-properties v-if="modal.resourceType === 'MYSQL'" ref='PropertiesModal'></mysql-properties>
      <postgresql-properties v-if="modal.resourceType === 'POSTGRESQL'" ref='PropertiesModal'></postgresql-properties>
      <http-client-properties v-if="modal.resourceType === 'HTTPCLIENT'" ref='PropertiesModal'></http-client-properties>
      <http-server-properties v-if="modal.resourceType === 'HTTPSERVER'" ref='PropertiesModal'></http-server-properties>
      <t-dengine-properties v-if="modal.resourceType === 'TDENGINE'" ref='PropertiesModal'></t-dengine-properties>
      <sqlserver-properties v-if="modal.resourceType === 'SQLSERVER'" ref='PropertiesModal'></sqlserver-properties>
      <opc-u-a-properties v-if="modal.resourceType === 'OPCUA'" ref='PropertiesModal'></opc-u-a-properties>
      <redis-properties v-if="modal.resourceType === 'REDIS'" ref='PropertiesModal'></redis-properties>
      <rabbit-m-q-properties v-if="modal.resourceType === 'RABBITMQ'" ref='PropertiesModal'></rabbit-m-q-properties>
      <tcp-properties v-if="modal.resourceType === 'TCP'" ref='PropertiesModal'></tcp-properties>
      <udp-properties v-if="modal.resourceType === 'UDP'" ref='PropertiesModal'></udp-properties>
      <snmp-properties v-if="modal.resourceType === 'SNMP'" ref='PropertiesModal'></snmp-properties>
      <modbus-tcp-properties v-if="modal.resourceType === 'MODBUSTCP'" ref='PropertiesModal'></modbus-tcp-properties>
      <timescale-d-b-properties v-if="modal.resourceType === 'TIMESCALEDB'" ref='PropertiesModal'></timescale-d-b-properties>
      <maria-d-b-properties v-if="modal.resourceType === 'MARIADB'" ref='PropertiesModal'></maria-d-b-properties>
      <rocket-m-q-properties v-if="modal.resourceType === 'ROCKETMQ'" ref='PropertiesModal'></rocket-m-q-properties>
      <active-m-q-properties v-if="modal.resourceType === 'ACTIVEMQ'" ref='PropertiesModal'></active-m-q-properties>
      <pulsar-properties v-if="modal.resourceType === 'PULSAR'" ref='PropertiesModal'></pulsar-properties>
      <a-form-model-item label='备注' prop='description'>
        <a-textarea v-model='modal.description' :rows='4' placeholder='请输入备注'
        />
      </a-form-model-item>
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
      <a-button :style="{ marginRight: '10px',width: '110px' }" @click='testDriver'> 测试</a-button>
      <a-button :style="{ marginRight: '10px',width: '110px' }" @click='onClose'> 取消</a-button>
      <a-button :style="{ width: '110px' }" @click='handleOk' type='primary'> 保存</a-button>
    </div>
  </a-drawer>
</template>

<script>
import { postAction, putAction } from '@/api/manage'
import { resourceTypeAllList } from '@/config/resource.config'
import MqttProperties from '../properties/MqttProperties'
import KafkaProperties from '../properties/KafkaProperties'
import MysqlProperties from '../properties/MysqlProperties'
import PostgresqlProperties from '../properties/PostgresqlProperties'
import HttpClientProperties from '../properties/HttpClientProperties'
import HttpServerProperties from '../properties/HttpServerProperties'
import TDengineProperties from '../properties/TDengineProperties'
import SqlserverProperties from '../properties/SqlserverProperties'
import OpcUAProperties from '../properties/OpcUAProperties'
import RedisProperties from '../properties/RedisProperties'
import RabbitMQProperties from '../properties/RabbitMQProperties'
import TcpProperties from '../properties/TcpProperties'
import UdpProperties from '../properties/UdpProperties'
import SnmpProperties from '../properties/SnmpProperties'
import ModbusTcpProperties from '../properties/ModbusTcpProperties'
import TimescaleDBProperties from '../properties/TimescaleDBProperties'
import MariaDBProperties from '../properties/MariaDBProperties'
import RocketMQProperties from '../properties/RocketMQProperties'
import ActiveMQProperties from '../properties/ActiveMQProperties'
import PulsarProperties from '../properties/PulsarProperties'

export default {
  name: 'ResourceModel',
  components: {
    MqttProperties,
    KafkaProperties,
    MysqlProperties,
    PostgresqlProperties,
    HttpClientProperties,
    HttpServerProperties,
    TDengineProperties,
    SqlserverProperties,
    OpcUAProperties,
    RedisProperties,
    RabbitMQProperties,
    TcpProperties,
    UdpProperties,
    SnmpProperties,
    ModbusTcpProperties,
    TimescaleDBProperties,
    MariaDBProperties,
    RocketMQProperties,
    ActiveMQProperties,
    PulsarProperties
  },
  data() {
    return {
      title: '操作',
      visible: false,
      confirmLoading: false,
      modal: {},
      url: {
        add: '/api/resource/add',
        update: '/api/resource/update',
        test: '/api/resource/test'
      },
      rules: {
        resourceType: [{ required: true, message: '请选择资源类型', trigger: 'change' }],
        resourceName: [{ required: true, message: '请输入资源名称', trigger: 'blur' }]
      },
      resourceTypeAllList: resourceTypeAllList
    }
  },
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      this.modal = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        if (this.modal.resourceType) {
          this.$refs.PropertiesModal.set(this.modal.properties)
        }
      })
    },
    onClose() {
      this.visible = false
    },
    handleOk() {
      const that = this
      this.$refs.ruleForm.validate(valid => {
        if (!valid) return false
        this.$refs.PropertiesModal.get((checked, prop) => {
          if (!checked) return false
          that.confirmLoading = true
          that.modal.properties = prop
          let obj
          if (that.modal.resourceId) {
            obj = putAction(that.url.update, that.modal)
          } else {
            obj = postAction(that.url.add, that.modal)
          }
          obj.then(res => {
            if (res.code === 200) {
              that.$message.success('保存成功')
              that.$emit('ok')
            } else {
              that.$message.error('保存失败')
            }
          })
            .finally(() => {
              that.confirmLoading = false
              that.onClose()
            })
        })
      })
    },
    filterOption(input, option) {
      if (!option.componentOptions.children[0].text) {
        return false
      }
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },
    testDriver() {
      let that = this
      this.$refs.PropertiesModal.get((checked, prop) => {
        if (!checked) return false
        that.modal.properties = prop
        postAction(that.url.test, that.modal).then((res) => {
          if (res.code === 200 && res.data === true) {
            that.$message.success('资源可用')
          } else {
            that.$message.error('资源不可用')
          }
        })
      })
    }
  }
}
</script>

<style scoped>
</style>
