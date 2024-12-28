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

    <a-card :bordered='false' style='margin-bottom: 20px'>
      <a-row>
        <a-col :span='3'>
          <a-statistic title='数据总数' :value='runtime.total' />
        </a-col>
        <a-col :span='3'>
          <a-statistic title='解析成功' :value='runtime.analysisSuccessCount' />
        </a-col>
        <a-col :span='3'>
          <a-statistic title='发送成功' :value='runtime.publishSuccessCount' />
        </a-col>
        <a-col :span='3'>
          <a-statistic title='解析失败' :value='runtime.analysisFailCount' />
        </a-col>
        <a-col :span='3'>
          <a-statistic title='发送失败' :value='runtime.publishFailCount' />
        </a-col>
        <a-col :span='4'>
          <a-statistic title='最近处理' :value="runtime.lastTime ? runtime.lastTime : '—'"
                       :value-style='{fontSize: "21px"}' />
        </a-col>
        <a-col :span='5'>
          <a-statistic title='启动时间' :value="runtime.startTime ? runtime.startTime : '—'"
                       :value-style='{fontSize: "21px"}' />
        </a-col>
      </a-row>
    </a-card>

    <a-card :bordered='false' style='margin-bottom: 20px'>
      <a-descriptions title='规则信息' :column='2'>
        <a-descriptions-item label='规则名称'>{{ rule.ruleName }}</a-descriptions-item>
        <a-descriptions-item label='忽略空值'>{{ rule.ignoreNullValue === true ? '是' : '否' }}</a-descriptions-item>
        <a-descriptions-item label='解析方式'>
          <span v-if='rule.analysisMode==="SCRIPT"'><a @click='showScript'>{{ analysisModeMap[rule.analysisMode] }}</a></span>
          <span v-else>{{ analysisModeMap[rule.analysisMode] }}</span>
        </a-descriptions-item>
        <a-descriptions-item label='备注'> {{ rule.description ? rule.description : '无' }}</a-descriptions-item>
        <a-descriptions-item label='源数据' :span='2'> {{ getDetails(rule.sourceResource) }}</a-descriptions-item>
        <a-descriptions-item v-for='(item,index) in rule.destResourceList' label='目的地' :span='2' :key='index'>
          {{ getDetails(item) }}
        </a-descriptions-item>
      </a-descriptions>

    </a-card>

    <a-card :bordered='false' style='margin-bottom: 20px' v-if='variables.length>0'>
      <div class='title'>环境变量</div>
      <a-table :columns='varColumns' :data-source='variables' size='middle' :pagination='false'
               style='margin-bottom: 20px'>
      </a-table>
    </a-card>

    <a-card :bordered='false' style='margin-bottom: 20px'>
      <div class='title'>
        最近数据
        <span @click='refresh' style='cursor: pointer'><a-icon type='redo'></a-icon></span>
      </div>
      <a-table :columns='dataColumns' :data-source='runtime.lastData' size='middle' :pagination='false'>
        <span slot='receiveData' slot-scope='text'> {{ text ? text : '—' }} </span>

        <span slot='analysisSuccess' slot-scope='text,record'>
          <a-popover title='解析结果'>
            <template slot='content'>
              {{ record.analysisData ? record.analysisData : '—' }}
            </template>
              <span style='cursor: pointer'>
              <a-badge v-if='text===true' color='green' text='成功' />
              <a-badge v-if='text===false' color='green' text='失败' />
              </span>
          </a-popover>
        </span>

        <span slot='publishSuccess' slot-scope='text,record'>
          <a-popover title='发送数据'>
            <template slot='content'>
              {{ record.publishData ? record.publishData : '—' }}
            </template>
            <span style='cursor: pointer'>
              <a-badge v-if='text===true' color='green' text='成功' />
              <a-badge v-if='text===false' color='green' text='失败' />
            </span>
          </a-popover>
        </span>

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
      variables: [],
      analysisModeMap: analysisModeMap,
      dataColumns: [
        {
          title: '时间',
          dataIndex: 'time'
        },
        {
          title: '接收数据',
          dataIndex: 'receiveData',
          scopedSlots: { customRender: 'receiveData' }
        },
        {
          title: '解析',
          dataIndex: 'analysisSuccess',
          scopedSlots: { customRender: 'analysisSuccess' }
        },
        {
          title: '发送',
          dataIndex: 'publishSuccess',
          scopedSlots: { customRender: 'publishSuccess' }
        },
        {
          title: '说明',
          dataIndex: 'message'
        }
      ],
      varColumns: [
        {
          title: '变量名称',
          dataIndex: 'name',
          width: '30%'
        },
        {
          title: '初始值',
          dataIndex: 'initValue'
        },
        {
          title: '当前值',
          dataIndex: 'value'
        }
      ]
    }
  },
  mounted() {
    this.ruleId = this.$route.params.ruleId
    this.getInfo()
  },
  computed: {
    title() {
      return this.$route.meta.title
    }
  },
  methods: {
    showScript() {
      this.$refs.ScriptViewModel.show(this.rule.script)
    },
    refresh() {
      this.getInfo()
      this.$message.success('刷新成功')
    },
    getInfo() {
      if (this.ruleId) {
        getAction(this.url.rule, { ruleId: this.ruleId }).then(res => {
          this.rule = res.data
          if (!this.rule) return

          getAction(this.url.runtime, { ruleId: this.ruleId }).then(res => {
            this.runtime = res.data

            this.variables = []
            if (!this.runtime.variables) return
            let keys = Object.keys(this.runtime.variables)
            for (let key of keys) {
              this.variables.push({
                name: key,
                value: this.runtime.variables[key],
                initValue: this.rule.variables[key]
              })
            }

          })


        })
      }
    },
    onClose() {
      this.$router.push({ name: 'ruleList' })
    },
    getDetails(resource) {
      if (!resource) return ''
      let resourceDetails = getResourceDetails(resource, 'rule')
      let result = '\xa0类型:\xa0' + resourceTypeMap[resource.resourceType] + '\xa0\xa0\xa0\xa0\xa0名称:\xa0' + resource.resourceName
      resourceDetails.forEach(detail => {
        result += '\xa0\xa0\xa0\xa0\xa0' + detail.name + ':\xa0' + detail.value
      })
      return result
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
