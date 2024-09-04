<template>
  <page-header-wrapper :breadcrumb='false'>
    <a-card style='margin-bottom: 15px' :body-style='{paddingBottom:0}'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='20'>
            <a-col :md='7' :sm='24'>
              <a-form-item label='规则名称'>
                <a-input v-model='queryParam.ruleName' placeholder='请输入规则名称' />
              </a-form-item>
            </a-col>
            <a-col :md='14' :sm='24'>
              <a-button type='primary' @click='loadData'>查询</a-button>
              <a-button style='margin-left: 8px' @click='reset'>重置</a-button>
            </a-col>
            <a-col :md='3' :sm='24' style='text-align: right'>
              <a-button type='primary' @click='handleAdd()' icon='plus'>新建规则</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </a-card>

    <a-card :body-style='{minHeight:"500px"}'>
      <a-list
        :grid='{ gutter: 20, lg: 3, md: 1, sm: 1, xs: 1 }'
        :loading='loading'
        :data-source='data'
      >
        <a-list-item slot='renderItem' slot-scope='item'>
          <a-card hoverable>
            <div slot='title'>{{ item.ruleName }}</div>
            <div slot='extra'>
              <!--              <a-badge v-if='item.enable' color='green' text='运行中' style='color: #206bdc' />-->
              <a-tag v-if='item.enable' color='#1890ff'>运行中</a-tag>
              <a-tag v-if='!item.enable' color='#c2c0c0'>未启动</a-tag>
              <!--                <a-badge v-if='!item.enable' color='black' text='未启动' />-->
            </div>
            <a-row>
              <a-col :span='5'>
                <div>源数据：</div>
              </a-col>
              <a-col :span='19'>
                <div>{{ item.sourceResource.resourceName }}</div>
              </a-col>
            </a-row>
            <a-row>
              <a-col :span='5'>
                <div>目的资源：</div>
              </a-col>
              <a-col :span='19'>
                <div>{{ getDestListNameStr(item.destResourceList) }}</div>
              </a-col>
            </a-row>

            <span slot='actions'>
                 <a v-if='!item.enable' @click='handleStart(item)'>启动</a>
                 <a v-if='item.enable' @click='handleStop(item)'>停止</a>
              </span>
            <a slot='actions' @click='handleEdit(item)' v-if='!item.enable'>编辑</a>
            <a slot='actions' @click='handleInfo(item)' v-if='item.enable'>状态</a>
            <a-popconfirm slot='actions' title='确定删除此规则?' @confirm='() => handleDelete(item)' v-if='!item.enable'>
              <a href='javascript:;'>删除</a>
            </a-popconfirm>
          </a-card>
        </a-list-item>
      </a-list>
    </a-card>
    <runtime-model ref='RuntimeModel'></runtime-model>
  </page-header-wrapper>
</template>

<script>
import { postAction, getAction } from '@/api/manage'
import RuntimeModel from './modules/RuntimeModel'

export default {
  name: 'RuleList',
  components: { RuntimeModel },
  data() {
    return {
      loading: true,
      data: [],
      queryParam: {},
      url: {
        list: '/api/rule/list',
        remove: '/api/rule/remove',
        update: '/api/rule/update',
        start: '/api/rule/start',
        stop: '/api/rule/stop',
        runtime: '/api/rule/runtime'
      }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    handleAdd() {
      this.$router.push({ name: 'ruleInfo', params: { ruleId: undefined } })
    },
    handleEdit(record) {
      this.$router.push({ name: 'ruleInfo', params: { ruleId: record.ruleId } })
    },
    loadData() {
      this.loading = true
      postAction(this.url.list, this.queryParam).then(res => {
        this.data = res.data
        // this.data.unshift({})
        this.loading = false
      })
    },
    handleStart(item) {
      postAction(this.url.start, item).then(res => {
        if (res.code === 200) {
          this.$message.success('启动成功')
          this.loadData()
        } else {
          this.$message.info(res.message)
        }
      })
    },
    handleStop(item) {
      postAction(this.url.stop, item).then(res => {
        if (res.code === 200) {
          this.$message.success('停止成功')
          this.loadData()
        } else {
          this.$message.info(res.message)
        }
      })
    },
    handleDelete(item) {
      postAction(this.url.remove, item).then(res => {
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        } else {
          this.$message.info(res.message)
        }
      })
    },
    handleInfo(item) {
      this.$refs.RuntimeModel.show(item.ruleId)
    },
    getDestListNameStr(destList) {
      let str = []
      for (let i = destList.length - 1; i >= 0; i--) {
        str.push(destList[i].resourceName)
      }
      return str.join(',')
    },
    reset() {
      this.queryParam = {}
      this.loadData()
    }
  }
}
</script>

