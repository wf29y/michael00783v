<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24' v-if='type==="source"'>
        <a-form-model-item label='时间单位' prop='timeUnit'>
          <a-select v-model='properties.timeUnit' placeholder='请选择时间单位' style='width: 100%'>
            <a-select-option v-for='(item,index) in timeUnitList' :key='index' :value='item.value'>{{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='type==="source"'>
        <a-form-model-item label='启动延迟' prop='initialDelay'>
          <a-input-number v-model='properties.initialDelay' placeholder='请输入启动延迟' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='type==="source"'>
        <a-form-model-item label='读取频率' prop='period'>
          <a-input-number v-model='properties.period' placeholder='请输入读取频率' style='width: 100%' />
        </a-form-model-item>
      </a-col>
<!--      <a-col :span='24'>
        <a-form-model-item label='读取点位' prop='points'>
          <snmp-points-model ref='SnmpPointsModel'></snmp-points-model>
        </a-form-model-item>
      </a-col>-->
    </a-form-model>
  </a-row>
</template>

<script>

//import SnmpPointsModel from '../points/model/SnmpPointsModel'
import { timeUnitList } from '@/config/time.config'


export default {
  components: {
   // SnmpPointsModel
  },
  data() {
    return {
      properties: {
        points: []
      },
      timeUnitList: timeUnitList,
      rules: {
        //points: [{ required: true, validator: this.checkPoints, message: '请配置读取点位', trigger: 'blur' }],
        timeUnit: [{ required: true, message: '请选择时间单位', trigger: 'change' }],
        initialDelay: [{ required: true, message: '请输入启动延迟', trigger: 'blur' }],
        period: [{ required: true, message: '请输入读取频率', trigger: 'blur' }]
      }
    }
  },
  props: {
    type: { // dest or source
      type: String,
      default: undefined
    }
  },
  mounted() {
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
      // this.$nextTick(() => {
      //   this.$refs.SnmpPointsModel.set(this.properties.points)
      // })
    },
    get(callback) {
     // this.properties.points = this.$refs.SnmpPointsModel.get()
      let that = this
      this.$refs.propForm.validate(valid => {
        if (valid) {
          return callback(true, that.properties)
        } else {
          return callback(false)
        }
      })
    },
    // checkPoints(rule, value, callback) {
    //   let content = this.$refs.SnmpPointsModel.get()
    //   if (!content || content.length === 0) {
    //     return callback(new Error())
    //   } else {
    //     return callback()
    //   }
    // }
  }
}
</script>

<style>


</style>
