<template>
  <div>

    <a-card :bordered='false' style='margin-bottom: 20px' :body-style='{padding:"17px 24px"}'>
      <a-row>
        <a-col :span='12' style='font-size: 16px;font-weight: bold;color:rgba(0, 0, 0, 0.85);padding-top: 4px'>
          {{ rule.ruleName }}
        </a-col>
        <a-col :span='12' style='text-align: right'>
          <a-button @click='refresh' style='width:90px;margin-right: 8px'> 刷新</a-button>
          <a-button @click='editRule' style='width:90px;margin-right: 8px'> 编辑</a-button>
          <a-button @click='onClose' style='width:90px;' type='primary'> 返回</a-button>
        </a-col>
      </a-row>
    </a-card>

    <a-card :bordered='false' style='margin-bottom: 20px'>
      <a-row>
        <a-col :span='3'>
          <a-statistic title='累计总数' :value='runtime.total' />
        </a-col>
        <a-col :span='3'>
          <a-statistic title='转换成功' :value='runtime.transformSuccessCount' />
        </a-col>
        <a-col :span='3'>
          <a-statistic title='发送成功' :value='runtime.publishSuccessCount' />
        </a-col>
        <a-col :span='3'>
          <a-statistic title='转换失败' :value='runtime.transformFailCount' />
        </a-col>
        <a-col :span='3'>
          <a-statistic title='发送失败' :value='runtime.publishFailCount' />
        </a-col>
        <a-col :span='4'>
          <a-statistic title='最近执行' :value="runtime.lastTime ? runtime.lastTime : '—'"
                       :value-style='{fontSize: "21px"}' />
        </a-col>
        <a-col :span='5'>
          <a-statistic title='首次启动' :value="runtime.startTime ? runtime.startTime : '—'"
                       :value-style='{fontSize: "21px"}' />
        </a-col>
      </a-row>
    </a-card>

    <a-card :bordered='false' style='margin-bottom: 20px'>
      <a-descriptions title='规则信息' :column='2'>
        <a-descriptions-item label='规则名称'>{{ rule.ruleName }}</a-descriptions-item>
        <a-descriptions-item label='忽略空值'>{{ rule.ignoreNullValue === true ? '是' : '否' }}</a-descriptions-item>
        <a-descriptions-item label='转换方式'>
          <span v-if='rule.transformMode==="SCRIPT"'><a @click='showScript'>{{ transformModeMap[rule.transformMode] }}</a></span>
          <span v-else>{{ transformModeMap[rule.transformMode] }}</span>
        </a-descriptions-item>
        <a-descriptions-item label='备注'> {{ rule.description ? rule.description : '无' }}</a-descriptions-item>
        <!--        <a-descriptions-item label='源数据' :span='2'> {{ getDetails(rule.sourceResource) }}</a-descriptions-item>
                <a-descriptions-item v-for='(item,index) in rule.destResourceList' label='目的地' :span='2' :key='index'>
                  {{ getDetails(item) }}
                </a-descriptions-item>-->
      </a-descriptions>

    </a-card>


    <a-card style='margin-bottom: 20px' :bordered='false'>
      <div class='title'>数据源</div>
      <a-row style='background-color: #f6f6f6;padding: 15px 10px 0 15px' v-if='rule.sourceResource'>
        <a-col :span='18'>
          <a-descriptions :column='2'>
            <a-descriptions-item label='资源名称'>
              {{ rule.sourceResource.resourceName }}
            </a-descriptions-item>
            <a-descriptions-item label='资源类型'>
              {{ resourceTypeMap[rule.sourceResource.resourceType] }}
            </a-descriptions-item>
            <a-descriptions-item v-for='(element,index) in getDetails(rule.sourceResource)' :key='index'
                                 :label='element.name'>
              {{ element.value }}
            </a-descriptions-item>
          </a-descriptions>
        </a-col>
      </a-row>
    </a-card>


    <a-card style='margin-bottom: 20px' :bordered='false' :body-style='{paddingBottom:"10px"}'>
      <div class='title'>目标资源</div>
      <a-list :grid="{ gutter: 16, xs: 1, sm: 1, md: 1, lg: 1, xl: 1, xxl: 1 }" :data-source='rule.destResourceList' v-if='rule.destResourceList && rule.destResourceList.length>0'>
        <a-list-item slot='renderItem' slot-scope='resource,index'>
          <a-row style='background-color: #f6f6f6;padding: 15px 10px 0 15px'>
            <a-col :span='18'>
              <a-descriptions :column='2'>
                <a-descriptions-item label='资源名称'>
                  {{ resource.resourceName }}
                </a-descriptions-item>
                <a-descriptions-item label='资源类型'>
                  {{ resourceTypeMap[resource.resourceType] }}
                </a-descriptions-item>
                <a-descriptions-item v-for='(element,index) in getDetails(resource)' :key='index'
                                     :label='element.name'>
                  {{ element.value }}
                </a-descriptions-item>
              </a-descriptions>
            </a-col>
          </a-row>

        </a-list-item>
      </a-list>
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

        <span slot='transformSuccess' slot-scope='text,record'>
          <a-popover title='转换结果'>
            <template slot='content'>
              {{ record.transformData ? record.transformData : '—' }}
            </template>
              <span style='cursor: pointer'>
              <a-badge v-if='text===true' color='green' text='成功' />
              <a-badge v-if='text===false' color='red' text='失败' />
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
              <a-badge v-if='text===false' color='red' text='失败' />
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
import { transformModeMap } from '@/config/rule.config'
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
      transformModeMap: transformModeMap,
      resourceTypeMap: resourceTypeMap,
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
          title: '转换',
          dataIndex: 'transformSuccess',
          scopedSlots: { customRender: 'transformSuccess' }
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
    editRule() {
      this.$router.push({ path: '/rule/info/' + this.rule.ruleId })
    },
    getInfo() {
      if (this.ruleId) {
        getAction(this.url.rule, { ruleId: this.ruleId }).then(res => {
          this.rule = res.data
          if (!this.rule) return

          getAction(this.url.runtime, { ruleId: this.ruleId }).then(res => {
            this.runtime = res.data

            this.variables = []
            if (!this.rule.variables) return
            let keys = Object.keys(this.rule.variables)
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
      return getResourceDetails(resource, 'rule')
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
