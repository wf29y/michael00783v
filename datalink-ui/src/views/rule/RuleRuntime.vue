<template>
  <div>
    <a-card :bordered='false' style='margin-bottom: 20px' :body-style='{padding:"15px 20px"}'>
      <a-row>
        <a-col :span='12' style='font-size: 16px;font-weight: bold;color:rgba(0, 0, 0, 0.85);padding-top: 3px'>
          {{ rule.ruleName }}
        </a-col>
        <a-col :span='12' style='text-align: right'>
          <a-button @click='refresh' icon='redo' style='margin-right: 8px'> 刷新</a-button>
          <a-button @click='onClose' icon='rollback' type='primary'> 返回</a-button>
        </a-col>

      </a-row>
    </a-card>

    <a-card :bordered='false'>
      <a-row>
        <a-col :span='4'>
          <a-statistic title='数据总数' :value='runtime.successCount + runtime.failCount' :value-style='{fontSize: "23px"}'/>
        </a-col>
        <a-col :span='4'>
          <a-statistic title='成功数量' :value='runtime.successCount' :value-style='{fontSize: "23px"}'/>
        </a-col>
        <a-col :span='4'>
          <a-statistic title='失败数量' :value='runtime.failCount' :value-style='{fontSize: "23px"}'/>
        </a-col>
        <a-col :span='5'>
          <a-statistic title='启动时间' :value='runtime.startTime' :value-style='{fontSize: "21px"}'/>
        </a-col>
        <a-col :span='5'>
          <a-statistic title='最近处理' :value="runtime.lastTime ? runtime.lastTime : '无'"  :value-style='{fontSize: "21px"}'/>
        </a-col>
      </a-row>

      <a-divider style='margin-bottom: 32px' />

      <a-descriptions title='规则信息' :column='4'>
        <a-descriptions-item label='规则名称'>{{ rule.ruleName }}</a-descriptions-item>
        <a-descriptions-item label='解析方式'>
          <span v-if='rule.analysisMode==="SCRIPT"'><a @click='showScript'>{{ analysisModeMap[rule.analysisMode] }}</a></span>
          <span v-else>{{ analysisModeMap[rule.analysisMode] }}</span>
        </a-descriptions-item>
        <a-descriptions-item label='忽略空值'>{{ rule.ignoreNullValue === true ? '是' : '否' }}</a-descriptions-item>
        <a-descriptions-item label='备注'> {{ rule.description ? rule.description : '无' }}</a-descriptions-item>
        <a-descriptions-item label='源数据' :span='2'> {{ getDetails(rule.sourceResource) }}</a-descriptions-item>
        <a-descriptions-item v-for='(item,index) in rule.destResourceList' :label='"目的地" + (index+1)' :span='2' :key='index'>
          {{ getDetails(item) }}
        </a-descriptions-item>
      </a-descriptions>

      <a-divider style='margin-bottom: 32px' />

      <div class='title'>环境变量</div>
      <a-table :columns='varColumns' :data-source='variables' size='middle' :pagination='false' style='margin-bottom: 20px'>
      </a-table>

      <div class='title' style='padding-top: 10px'>
        最近数据
        <span @click='refresh' style='cursor: pointer'><a-icon type='redo'></a-icon></span>
      </div>
      <a-table :columns='dataColumns' :data-source='runtime.lastData' size='middle' :pagination='false'>
        <span slot='data' slot-scope='text'> {{ text }} </span>
      </a-table>


    </a-card>

    <script-view-model ref='ScriptViewModel'></script-view-model>
  </div>
</template>

<script>
import { getAction } from '@/api/manage'
import { resourceTypeMap, getResourceDetails } from '@/config/resource.config'
import { analysisModeMap } from '@/config/rule.config'
import ScriptViewModel from './modules/ScriptViewModel'

export default {
  components: {
    ScriptViewModel
  },
  data() {
    return {
      url: {
        runtime: '/api/runtime/info',
        rule: '/api/rule/info'
      },
      rule: {},
      runtime: {},
      variables:[],
      analysisModeMap: analysisModeMap,
      dataColumns: [
        {
          title: '时间',
          dataIndex: 'time',
          width: '30%'
        },
        {
          title: '数据',
          dataIndex: 'data',
          scopedSlots: { customRender: 'data' }
        }
      ],
      varColumns: [
        {
          title: '变量名称',
          dataIndex: 'name',
          width: '30%'
        },
        {
          title: '当前值',
          dataIndex: 'value'
        }
      ],
    }
  },
  mounted() {
    this.ruleId = this.$route.params.ruleId;
    this.getInfo()
  },
  computed: {
    title() {
      return this.$route.meta.title
    }
  },
  methods: {
    showScript(){
      this.$refs.ScriptViewModel.show(this.rule.script)
    },
    refresh(){
      this.getInfo();
      this.$message.success("刷新成功")
    },
    getInfo(){
      if (this.ruleId) {
        getAction(this.url.runtime, { ruleId: this.ruleId }).then(res => {
          this.runtime = res.data

          this.variables = []
          if (!this.runtime.variables) return
          let keys = Object.keys(this.runtime.variables)
          for (let key of keys) {
            this.variables.push({
              name: key,
              value: this.runtime.variables[key]
            })
          }

        })
        getAction(this.url.rule, { ruleId: this.ruleId }).then(res => {
          this.rule = res.data
        })
      }
    },
    onClose() {
      this.$router.push({ name: 'ruleList' })
    },
    getDetails(resource) {
      let resourceDetails = getResourceDetails(resource, 'rule')
      return '\xa0类型:\xa0'+resourceTypeMap[resource.resourceType] + '\xa0\xa0\xa0\xa0\xa0名称:\xa0' +resource.resourceName + '\xa0\xa0\xa0\xa0\xa0' + resourceDetails.name + ':\xa0' + resourceDetails.value
    }
  }

}
</script>

<style lang='less' scoped>
.title {
  font-size: 16px;
  font-weight: bold;
  color: rgba(0, 0, 0, 0.85);
  margin-bottom: 16px;
}
</style>
