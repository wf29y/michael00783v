<template>
  <div class='input-cron'>
    <a-input :placeholder='placeholder' v-model='editCronValue' :disabled='disabled'>
      <span slot='addonAfter' @click='showConfigDlg' class='config-btn' :disabled='disabled'>
        <a-icon type='setting'></a-icon>
        选择
      </span>
    </a-input>
    <a-modal :visible.sync='show' title='Cron表达式' width='800px' :closable='false' :bodyStyle='{paddingTop:"10px"}'>
      <easy-cron
        v-model='editCronValue'
        :exeStartTime='exeStartTime'
        :hideYear='hideYear'
        :remote='remote'
        :hideSecond='hideSecond'
        style='width: 100%'
      ></easy-cron>
      <div slot='footer'>
        <a-button @click='reset'>重置</a-button>
        <a-button type='primary' @click='show=false'>确定</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script>
import EasyCron from './EasyCron.vue'

export default {
  name: 'input-cron',
  components: { EasyCron },
  model: {
    prop: 'cronValue',
    event: 'change'
  },
  props: {
    cronValue: {
      type: String,
      default: ''
    },
    width: {
      type: String,
      default: '800px'
    },
    placeholder: {
      type: String,
      default: '请输入Cron表达式'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    exeStartTime: {
      type: [Number, String, Object],
      default: 0
    },
    hideSecond: {
      type: Boolean,
      default: false
    },
    hideYear: {
      type: Boolean,
      default: false
    },
    remote: {
      type: Function,
      default: null
    }
  },
  data() {
    return {
      editCronValue: this.cronValue,
      show: false,
      initData: undefined
    }
  },
  watch: {
    cronValue(newVal, oldVal) {
      if (newVal === this.editCronValue) {
        return
      }
      this.editCronValue = newVal
    },
    editCronValue(newVal, oldVal) {
      this.$emit('change', newVal)
    }
  },
  methods: {
    showConfigDlg() {
      if (!this.disabled) {
        this.show = true
        this.initData = this.editCronValue
      }
    },
    reset(){
      this.editCronValue = this.initData
    }
  }
}
</script>

<style scoped>

.config-btn {
  cursor: pointer;
}

</style>
