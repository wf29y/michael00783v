<template>
  <a-modal
    :confirmLoading='confirmLoading'
    :title='resourceMode==="source"?"数据源":"目标资源"'
    :width='550'
    :visible='visible'
    @cancel='onClose'
    :destroyOnClose='true'
  >
    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>
      <a-form-model-item label='类型' prop='resourceType'>
        <a-select v-model='modal.resourceType' placeholder='请选择资源类型' @change='resourceTypeChange' :disabled='resourceIndex >= 0'>
          <a-select-opt-group v-for='(group,groupIndex) in resourceTypeList' :key='groupIndex' :label='group.group'>
            <a-select-option v-for='(item,itemIndex) in group.list' :value='item.code' :key='itemIndex'>{{ item.name
              }}
            </a-select-option>
          </a-select-opt-group>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label='资源' prop='resourceId' v-show='modal.resourceType'>
        <a-select v-model='modal.resourceId' placeholder='请选择资源' @change='resourceChange'>
          <a-select-option v-for='(item,index) in resourceList' :value='item.resourceId' :key='index'>
            {{ item.resourceName }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <mqtt-properties v-if="modal.resourceType === 'MQTT'" ref='PropertiesModal' :type='resourceMode'></mqtt-properties>
      <kafka-properties v-if="modal.resourceType === 'KAFKA'" ref='PropertiesModal' :type='resourceMode'></kafka-properties>
      <MysqlProperties v-if="modal.resourceType === 'MYSQL'" ref='PropertiesModal' :type='resourceMode'></MysqlProperties>
      <postgresql-properties v-if="modal.resourceType === 'POSTGRESQL'" ref='PropertiesModal' :type='resourceMode'></postgresql-properties>
      <http-client-properties v-if="modal.resourceType === 'HTTPCLIENT'" ref='PropertiesModal' :type='resourceMode'></http-client-properties>
      <http-server-properties  v-if="modal.resourceType === 'HTTPSERVER'" ref='PropertiesModal' :type='resourceMode'></http-server-properties>
      <t-dengine-properties v-if="modal.resourceType === 'TDENGINE'" ref='PropertiesModal' :type='resourceMode'></t-dengine-properties>
      <sqlserver-properties v-if="modal.resourceType === 'SQLSERVER'" ref='PropertiesModal' :type='resourceMode'></sqlserver-properties>
      <opc-u-a-properties v-if="modal.resourceType === 'OPCUA'" ref='PropertiesModal' :type='resourceMode'></opc-u-a-properties>
      <redis-properties v-if="modal.resourceType === 'REDIS'" ref='PropertiesModal' :type='resourceMode'></redis-properties>
      <rabbit-m-q-properties v-if="modal.resourceType === 'RABBITMQ'" ref='PropertiesModal' :type='resourceMode'></rabbit-m-q-properties>
      <tcp-properties v-if="modal.resourceType === 'TCP'" ref='PropertiesModal' :type='resourceMode'></tcp-properties>
      <udp-properties v-if="modal.resourceType === 'UDP'" ref='PropertiesModal' :type='resourceMode'></udp-properties>
      <snmp-properties v-if="modal.resourceType === 'SNMP'" ref='PropertiesModal' :type='resourceMode'></snmp-properties>
      <modbus-tcp-properties v-if="modal.resourceType === 'MODBUSTCP'" ref='PropertiesModal' :type='resourceMode'></modbus-tcp-properties>
      <timescale-d-b-properties v-if="modal.resourceType === 'TIMESCALEDB'" ref='PropertiesModal' :type='resourceMode'></timescale-d-b-properties>
      <maria-d-b-properties v-if="modal.resourceType === 'MARIADB'" ref='PropertiesModal' :type='resourceMode'></maria-d-b-properties>
      <active-m-q-properties v-if="modal.resourceType === 'ACTIVEMQ'" ref='PropertiesModal' :type='resourceMode'></active-m-q-properties>
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
      <a-button :style="{ marginRight: '8px' }" @click='onClose'> 取消</a-button>
      <a-button type='primary' @click='handleOk'> 确定</a-button>
    </div>
  </a-modal>
</template>

<script>
import { postAction } from '@/api/manage'
import { getResourceListByType } from '@/config/resource.config'
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
    ActiveMQProperties
  },
  data() {
    return {
      title: '操作',
      visible: false,
      confirmLoading: false,
      modal: {},
      url: {
        resourceList: '/api/resource/list'
      },
      rules: {
        resourceType: [{ required: true, message: '请选择资源类型', trigger: 'change' }],
        resourceId: [{ required: true, message: '请选择资源', trigger: 'change' }]
      },
      resourceList: [],
      resourceTypeList: [],
      resourceMode: null, // source or dest
      resourceIndex: -1 // source or dest
    }
  }
  ,
  mounted() {
  }
  ,
  methods: {
    add(resourceMode) {
      this.edit(resourceMode, {}, -1)
    }
    ,
    edit(resourceMode, record, resourceIndex) {
      this.resourceMode = resourceMode
      this.resourceIndex = resourceIndex
      this.modal = JSON.parse(JSON.stringify(record))
      this.resourceTypeList = getResourceListByType(resourceMode)
      this.visible = true
      this.$nextTick(() => {
        if (this.modal.resourceType) {
          this.$refs.PropertiesModal.set(this.modal.properties)
          this.listResource(this.modal.resourceType)
        }
      })
    }
    ,
    listResource(type) {
      postAction(this.url.resourceList, { resourceType: type }).then(res => {
        this.resourceList = res.data
      })
    }
    ,
    onClose() {
      this.visible = false
    }
    ,
    handleOk() {
      const that = this
      this.$refs.ruleForm.validate(valid => {
        if (!valid) return false
        that.$refs.PropertiesModal.get((checked, prop) => {
          if (!checked) return false
          that.confirmLoading = true
          that.modal.properties = prop
          if (that.resourceIndex >= 0) {
            that.$emit('update', this.resourceMode, this.modal, this.resourceIndex)
          } else {
            that.$emit('add', this.resourceMode, this.modal)
          }
          that.confirmLoading = false
          that.visible = false
        })
      })
    }
    ,
    filterOption(input, option) {
      if (!option.componentOptions.children[0].text) {
        return false
      }
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    }
    ,
    resourceTypeChange(type) {
      this.modal = { resourceType: type }
      this.listResource(type)
    }
    ,
    resourceChange(resourceId) {
      let index = this.resourceList.findIndex(resource => resource.resourceId === resourceId)
      this.modal = this.resourceList[index]
      this.$nextTick(() => {
        if (this.modal.resourceType) {
          this.$refs.PropertiesModal.set(this.modal.properties)
        }
      })
    }
  }
}
</script>

<style scoped>


</style>
