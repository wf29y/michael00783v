<template>
  <page-header-wrapper>
    <a-card :bordered='false'>
      <a-list
        :grid='{ gutter: 24, lg: 4, md: 2, sm: 1, xs: 1 }'
        :loading='loading'
        :data-source='data'
      >
        <a-list-item slot='renderItem' slot-scope='item'>
          <template v-if='!item || item.resourceId === undefined'>
            <a-button @click='handleAdd()' class='new-btn' type='dashed' style='height: 194px'>
              <a-icon type='plus' />
              新增资源
            </a-button>
          </template>
          <template v-else>
            <a-card hoverable>
              <div slot='title'>{{ item.resourceName }}</div>
              <a-row :gutter='16'>
                <a-col :span='7'>
                  <div>资源ID：</div>
                </a-col>
                <a-col :span='12'>
                  <div>{{ item.resourceId }}</div>
                </a-col>
              </a-row>
              <a-row :gutter='16'>
                <a-col :span='7'>
                  <div>资源类型：</div>
                </a-col>
                <a-col :span='12'>
                  <div>{{ resourceTypeMap[item.resourceType] }}</div>
                </a-col>
              </a-row>

              <a slot='actions' @click='handleEdit(item)'>编辑</a>
              <a-popconfirm slot='actions' title='确定删除此资源?' @confirm='() => handleDelete(item)'>
                <a href='javascript:;'>删除</a>
              </a-popconfirm>
            </a-card>
          </template>
        </a-list-item>
      </a-list>
      <resource-model ref='ResourceModel' @ok='loadData'></resource-model>
    </a-card>
  </page-header-wrapper>
</template>

<script>
import { postAction, putAction } from '@/api/manage'
import ResourceModel from './modules/ResourceModel'
import { resourceTypeMap } from '@/config/resource.config'

export default {
  name: 'ResourceList',
  components: {
    ResourceModel
  },
  data() {
    return {
      loading: true,
      data: [],
      url: {
        list: '/api/resource/list',
        remove: '/api/resource/remove',
        update: '/api/resource/update'
      },
      resourceTypeMap: resourceTypeMap
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    handleAdd() {
      this.$refs.ResourceModel.add()
    },
    handleEdit(record) {
      this.$refs.ResourceModel.edit(record)
    },
    loadData() {
      this.loading = true
      postAction(this.url.list, {}).then(res => {
        this.data = res.data
        this.data.unshift({})
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
    }
  }
}
</script>

<style lang='less' scoped>
@import '~@/components/index.less';

.card-list {
  /deep/ .ant-card-body:hover {
    .ant-card-meta-title > a {
      color: @primary-color;
    }
  }

  /deep/ .ant-card-meta-title {
    margin-bottom: 12px;

    & > a {
      display: inline-block;
      max-width: 100%;
      color: rgba(0, 0, 0, 0.85);
    }
  }

  /deep/ .meta-content {
    position: relative;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    height: 64px;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;

    margin-bottom: 1em;
  }
}

.card-avatar {
  width: 48px;
  height: 48px;
  border-radius: 48px;
}

.ant-card-actions {
  background: #f7f9fa;

  li {
    float: left;
    text-align: center;
    margin: 12px 0;
    color: rgba(0, 0, 0, 0.45);
    width: 50%;

    &:not(:last-child) {
      border-right: 1px solid #e8e8e8;
    }

    a {
      color: rgba(0, 0, 0, 0.45);
      line-height: 22px;
      display: inline-block;
      width: 100%;

      &:hover {
        color: @primary-color;
      }
    }
  }
}

.new-btn {
  background-color: #fff;
  border-radius: 2px;
  width: 100%;
  height: 188px;
}
</style>
