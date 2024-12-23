<template>
  <page-header-wrapper :breadcrumb='false'>
    <a-card style='margin-bottom: 15px' :body-style='{paddingBottom:0}' :bordered='false'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='20'>
            <a-col :md='7' :sm='24'>
              <a-form-item label='资源名称'>
                <a-input v-model='queryParam.resourceName' placeholder='请输入资源名称' />
              </a-form-item>
            </a-col>
            <a-col :md='14' :sm='24'>
              <a-button type='primary' @click='loadData'>查询</a-button>
              <a-button style='margin-left: 8px' @click='reset'>重置</a-button>
            </a-col>
            <a-col :md='3' :sm='24' style='text-align: right'>
              <a-button type='primary' @click='handleAdd()' icon='plus'>新建资源</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </a-card>

    <a-card :body-style='{minHeight:"500px"}' :bordered='false'>
      <a-list
        :grid='{ gutter: 20, lg: 4, md: 2, sm: 1, xs: 1 }'
        :loading='loading'
        :data-source='data'
      >
        <a-list-item slot='renderItem' slot-scope='item'>
          <a-card>
            <div slot='title'>{{ item.resourceName }}</div>
            <a-row>
              <a-col :span='7'>
                <div>资源类型：</div>
              </a-col>
              <a-col :span='12'>
                <div>{{ resourceTypeMap[item.resourceType] }}</div>
              </a-col>
            </a-row>
            <a-row>
              <a-col :span='7'>
                <div>{{ getDetails(item).name }}：</div>
              </a-col>
              <a-col :span='12'>
                <div>{{ getDetails(item).value }}</div>
              </a-col>
            </a-row>
            <a slot='actions' @click='handleEdit(item)'>编辑</a>
            <a-popconfirm slot='actions' title='确定删除此资源?' @confirm='() => handleDelete(item)'>
              <a href='javascript:;'>删除</a>
            </a-popconfirm>
          </a-card>
        </a-list-item>
      </a-list>
      <resource-model ref='ResourceModel' @ok='loadData'></resource-model>
    </a-card>
  </page-header-wrapper>
</template>

<script>
import { postAction, putAction } from '@/api/manage'
import ResourceModel from './modules/ResourceModel'
import { resourceTypeMap, getResourceDetails } from '@/config/resource.config'

export default {
  name: 'ResourceList',
  components: {
    ResourceModel
  },
  data() {
    return {
      loading: true,
      data: [],
      queryParam: {},
      url: {
        list: '/api/resource/list',
        remove: '/api/resource/remove',
        update: '/api/resource/update'
      },
      resourceTypeMap: resourceTypeMap
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    getDetails(resource) {
      return getResourceDetails(resource, 'resource')
    },
    handleAdd() {
      this.$refs.ResourceModel.add()
    },
    handleEdit(record) {
      this.$refs.ResourceModel.edit(record)
    },
    loadData() {
      this.loading = true
      postAction(this.url.list, this.queryParam).then(res => {
        this.data = res.data
        this.loading = false
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
    reset() {
      this.queryParam = {}
      this.loadData()
    }
  }
}
</script>
