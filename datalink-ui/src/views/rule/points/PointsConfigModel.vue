<template>
  <a-modal v-model='visible' title='点位配置' :width='650'>
    <template slot='footer'>
      <a-button key='back' @click='handleClose'>关闭</a-button>
      <a-button key='submit' type='primary' @click='handleOk' v-show='!disableEdit'>保存</a-button>
    </template>

    <opc-points-model ref='PointModel' v-if='resourceType === "OPCUA"' :disable-edit='disableEdit'></opc-points-model>
    <snmp-points-model ref='PointModel' v-if='resourceType === "SNMP"' :disable-edit='disableEdit'></snmp-points-model>

  </a-modal>
</template>

<script>
import OpcPointsModel from './model/OpcPointsModel'
import SnmpPointsModel from './model/SnmpPointsModel'

export default {
  name: 'PointsConfigModel',
  components: { OpcPointsModel, SnmpPointsModel },
  data() {
    return {
      visible: false,
      resourceType: null,
      resourceMode: null, // source or dest
      resourceIndex: -1, // source or dest
      disableEdit: false
    }
  },
  methods: {
    show(resourceType, points) {
      this.resourceType = resourceType
      this.$nextTick(() => {
        this.$refs.PointModel.set(points)
      })
      this.disableEdit = true
      this.visible = true
    },
    config(resourceType, resourceMode, resourceIndex, points) {
      this.resourceType = resourceType
      this.resourceMode = resourceMode
      this.resourceIndex = resourceIndex
      this.$nextTick(() => {
        this.$refs.PointModel.set(points)
      })
      this.disableEdit = false
      this.visible = true
    },
    handleOk() {
      this.$emit('update', this.resourceMode, this.resourceIndex, this.$refs.PointModel.get())
      this.handleClose()
    },
    handleClose() {
      this.visible = false
      this.resourceType = null
      this.resourceMode = null// source or dest
      this.resourceIndex = -1 // source or dest
    }
  }
}
</script>

<style scoped>

</style>