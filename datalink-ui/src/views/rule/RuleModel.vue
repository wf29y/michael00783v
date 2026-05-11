<template>
  <div>
    <a-form-model ref='ruleForm' :model='modal' layout='vertical' :rules='rules'>

      <a-card :bordered='false' style='margin-bottom: 24px' :body-style='{padding:"17px 24px"}'>
        <a-row>
          <a-col :span='12' style='font-size: 16px;font-weight: bold;color:rgba(0, 0, 0, 0.85);padding-top: 4px'>
            {{ modal.ruleId ? '编辑' : '新建' }}规则
          </a-col>
          <a-col :span='12' style='text-align: right'>
            <a-space size='small'>
              <a-button @click='onClose' style='width:75px'> 取消</a-button>
              <a-button @click='saveRule' style='width:75px' type='primary'> 保存</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-card>

      <a-card :body-style='{paddingBottom:0}' style='margin-bottom: 24px' :bordered='false'>
        <a-row :gutter='24'>
          <a-col :span='12'>
            <a-form-model-item label='名称' prop='ruleName'>
              <a-input v-model='modal.ruleName' placeholder='请输入规则名称' />
            </a-form-model-item>
          </a-col>
          <a-col :span='12'>
            <a-form-model-item label='备注'>
              <a-input v-model='modal.description' placeholder='请输入备注' />
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-card>


      <a-card style='margin-bottom: 24px' :bordered='false'>
        <div class='title'>数据源</div>
        <a-list :grid='{ gutter: 16, xs: 1, sm: 1, md: 1, lg: 1, xl: 1, xxl: 1 }'
                :data-source='modal.sourceResourceList'
                v-if='modal.sourceResourceList.length>0'>
          <a-list-item slot='renderItem' slot-scope='resource,index'>
            <a-row style='background-color: #f6f6f6;padding: 15px 10px 0 15px'>
              <a-col :span='20'>
                <a-descriptions :column='2'>
                  <a-descriptions-item label='名称'>
                    {{ resource.resourceName }}
                  </a-descriptions-item>
                  <a-descriptions-item label='资源ID'>
                    {{ resource.resourceRuntimeId }}
                  </a-descriptions-item>
                  <a-descriptions-item v-for='(element,index) in getDetails(resource)' :key='index'
                                       :label='element.name'>
                    {{ element.value }}
                  </a-descriptions-item>
                </a-descriptions>
              </a-col>
              <a-col :span='4' style='text-align: right'>
                <a v-if='resource.properties.points !== undefined' @click='pointConfig("source",resource,index)'>点位</a>
                <a-divider type='vertical' v-if='resource.properties.points !== undefined' />
                <a @click='freshResource("source",resource,index)'>刷新</a>
                <a-divider type='vertical' />
                <a @click='editResource("source",resource,index)'>配置</a>
                <a-divider type='vertical' />
                <a-popconfirm title='移除此资源?' @confirm='() => deleteResource("source",index)'>
                  <a href='javascript:;'>移除</a>
                </a-popconfirm>
              </a-col>
            </a-row>

          </a-list-item>
        </a-list>
        <a-button @click='addResource("source")' icon='plus'> 添加数据源</a-button>
      </a-card>


      <a-card style='margin-bottom: 24px' :bordered='false'>
        <div class='title'>数据转换</div>
        <draggable v-model='modal.transformList' :options='{animation:300}'>
          <transition-group>
            <a-row style='background-color: #f6f6f6;padding: 15px 10px 0 15px;margin-bottom: 16px;cursor: pointer'
                   :key='index+1' v-for='(transform,index) in modal.transformList'>
              <a-col :span='20'>
                <a-descriptions :column='2'>
                  <a-descriptions-item label='类型'>
                    {{ transformModeMap[transform.transformMode] }}
                  </a-descriptions-item>
                  <a-descriptions-item label='处理器'>
                    {{ transform.workerNum }}
                  </a-descriptions-item>
                  <a-descriptions-item label='执行顺序'>
                    {{ index + 1 }}
                  </a-descriptions-item>
                  <!--                  <a-descriptions-item label='转换ID'>
                                      {{ transform.transformRuntimeId }}
                                    </a-descriptions-item>-->
                  <a-descriptions-item label='SQL' v-if='transform.transformMode === "SQL"'>
                    {{ transform.properties.sql }}
                  </a-descriptions-item>
                  <a-descriptions-item label='脚本' v-if='transform.transformMode === "SCRIPT"'>
                    {{ subScript(transform.properties.script) }}
                  </a-descriptions-item>
                  <a-descriptions-item label='插件' v-if='transform.transformMode === "PLUGIN"'>
                    {{ transform.properties.plugin.pluginName }}
                  </a-descriptions-item>
                </a-descriptions>
              </a-col>
              <a-col :span='4' style='text-align: right'>
                <a @click='editTransform(transform,index)' v-show='transform.transformMode !=="WITHOUT"'>配置</a>
                <a-divider type='vertical' v-show='transform.transformMode !=="WITHOUT"' />
                <a-popconfirm title='移除此转换?' @confirm='() => deleteTransform(index)'>
                  <a href='javascript:;'>移除</a>
                </a-popconfirm>
              </a-col>
            </a-row>
          </transition-group>
        </draggable>
        <a-dropdown :trigger="['click']">
          <a-menu slot='overlay' @click='addTransform'>
            <a-menu-item v-for='transform in transformModeList' :key='transform.value'> {{ transform.name }}
            </a-menu-item>
          </a-menu>
          <a-button icon='plus'>添加数据转换</a-button>
        </a-dropdown>
      </a-card>


      <a-card style='margin-bottom: 24px' :bordered='false'>
        <div class='title'>目标资源</div>
        <a-list :grid='{ gutter: 16, xs: 1, sm: 1, md: 1, lg: 1, xl: 1, xxl: 1 }' :data-source='modal.destResourceList'
                v-if='modal.destResourceList.length>0'>
          <a-list-item slot='renderItem' slot-scope='resource,index'>
            <a-row style='background-color: #f6f6f6;padding: 15px 10px 0 15px'>
              <a-col :span='20'>
                <a-descriptions :column='2'>
                  <a-descriptions-item label='名称'>
                    {{ resource.resourceName }}
                  </a-descriptions-item>
                  <a-descriptions-item label='资源ID'>
                    {{ resource.resourceRuntimeId }}
                  </a-descriptions-item>
                  <a-descriptions-item v-for='(element,index) in getDetails(resource)' :key='index'
                                       :label='element.name'>
                    {{ element.value }}
                  </a-descriptions-item>
                </a-descriptions>
              </a-col>
              <a-col :span='4' style='text-align: right'>
                <a v-if='resource.properties.points !== undefined' @click='pointConfig("dest",resource,index)'>点位</a>
                <a-divider type='vertical' v-if='resource.properties.points !== undefined' />
                <a @click='freshResource("dest",resource,index)'>刷新</a>
                <a-divider type='vertical' />
                <a @click='editResource("dest",resource,index)'>配置</a>
                <a-divider type='vertical' />
                <a-popconfirm title='移除此资源?' @confirm='() => deleteResource("dest",index)'>
                  <a href='javascript:;'>移除</a>
                </a-popconfirm>
              </a-col>
            </a-row>

          </a-list-item>
        </a-list>
        <a-button @click='addResource("dest")' icon='plus'>添加目标资源</a-button>
      </a-card>

    </a-form-model>
    <resource-model ref='ResourceModel' @update='handleUpdateResource' @add='handleAddResource'></resource-model>
    <points-config-model ref='PointsConfigModel' @update='handleUpdatePoints'></points-config-model>

    <transform-script-model ref='TransformScriptModel' @update='handleUpdateTransform'
                            @add='handleAddTransform'></transform-script-model>
    <transform-plugin-model ref='TransformPluginModel' @update='handleUpdateTransform'
                            @add='handleAddTransform'></transform-plugin-model>
    <transform-sql-model ref='TransformSqlModel' @update='handleUpdateTransform'
                         @add='handleAddTransform'></transform-sql-model>
  </div>
</template>

<script>
import draggable from 'vuedraggable'
import { postAction, putAction, getAction } from '@/api/manage'
import MonacoEditor from '@/components/Editor/MonacoEditor'
import ResourceModel from './modules/ResourceModel'
import PointsConfigModel from './points/PointsConfigModel'
import { resourceTypeMap, getResourceDetails } from '@/config/resource.config'
import { transformModeMap, transformModeList } from '@/config/transform.config'
import TransformScriptModel from './transform/TransformScriptModel'
import TransformPluginModel from './transform/TransformPluginModel'
import TransformSqlModel from './transform/TransformSqlModel'


export default {
  components: {
    draggable,
    ResourceModel,
    MonacoEditor,
    PointsConfigModel,
    TransformScriptModel,
    TransformPluginModel,
    TransformSqlModel
  },
  data() {
    return {
      modal: {
        sourceResourceList: [],
        destResourceList: [],
        transformList: []
      },
      url: {
        add: '/api/rule/add',
        update: '/api/rule/update',
        info: '/api/rule/info',
        createId: '/api/rule/createId',
        resource: '/api/resource/info'
      },
      rules: {
        ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }]
      },
      resourceTypeMap: resourceTypeMap,
      transformModeMap: transformModeMap,
      transformModeList: transformModeList
    }
  },
  mounted() {
    let ruleId = this.$route.params.ruleId
    if (ruleId !== 'new') {
      this.ruleId = ruleId
      getAction(this.url.info, { ruleId: this.ruleId }).then(res => {
        let temp = res.data
        if (temp) {
          this.modal = res.data
        }
      })
    }
    //为了防止火狐浏览器拖拽的时候以新标签打开
    document.body.ondrop = function(event) {
      event.preventDefault()
      event.stopPropagation()
    }
  },
  methods: {
    getDetails(resource) {
      return getResourceDetails(resource, 'rule')
    },
    // 操作
    addResource(mode) {
      this.$refs.ResourceModel.add(mode)
    },
    editResource(mode, resource, index) {
      this.$refs.ResourceModel.edit(mode, resource, index)
    },
    deleteResource(mode, index) {
      if (mode === 'dest') {
        this.modal.destResourceList.splice(index, 1)
      } else if (mode === 'source') {
        this.modal.sourceResourceList.splice(index, 1)
      }
    },
    freshResource(mode, resource, index) {
      getAction(this.url.resource, { resourceId: resource.resourceId }).then((res) => {
        if (res.code === 200) {
          let tempProperties = Object.assign({}, resource.properties, res.data.properties)
          let result = Object.assign({}, resource, res.data)
          result.properties = tempProperties
          result.resourceRuntimeId = resource.resourceRuntimeId
          this.handleUpdateResource(mode, result, index)
          this.$message.success('刷新成功')
        } else {
          this.$message.error('刷新失败')
        }
      })
    },
    handleAddResource(mode, resource) {
      if (mode === 'dest') {
        let ids = this.modal.destResourceList.map(x => x.resourceRuntimeId.split('_')[2])
        postAction(this.url.createId, ids).then((res) => {
          resource.resourceRuntimeId = 'dest_' + resource.resourceType.toLowerCase() + '_' + res.data.id
          this.modal.destResourceList.push(resource)
        })
      } else if (mode === 'source') {
        let ids = this.modal.sourceResourceList.map(x => x.resourceRuntimeId.split('_')[2])
        postAction(this.url.createId, ids).then((res) => {
          resource.resourceRuntimeId = 'source_' + resource.resourceType.toLowerCase() + '_' + res.data.id
          this.modal.sourceResourceList.push(resource)
        })
      }
    },
    handleUpdateResource(mode, resource, index) {
      if (mode === 'dest') {
        this.$set(this.modal.destResourceList, index, resource)
      } else if (mode === 'source') {
        this.$set(this.modal.sourceResourceList, index, resource)
      }
    },
    // 打开点位配置
    pointConfig(mode, resource, index) {
      this.$refs.PointsConfigModel.config(resource.resourceType, mode, index, resource.properties.points)
    },
    // 更新点位配置
    handleUpdatePoints(mode, index, points) {
      if (mode === 'dest') {
        this.modal.destResourceList[index].properties.points = points
      } else if (mode === 'source') {
        this.modal.sourceResourceList[index].properties.points = points
      }
    },

    // 数据转换
    addTransform({ key }) {
      switch (key) {
        case 'WITHOUT': {
          if (this.modal.transformList.length > 0) break // 仅可在无转换的时候添加一个透传处理器
          this.handleAddTransform({
            workerNum: 3,
            transformMode: 'WITHOUT'
          })
          break
        }
        case 'SCRIPT': {
          this.$refs.TransformScriptModel.add()
          break
        }
        case 'PLUGIN': {
          this.$refs.TransformPluginModel.add()
          break
        }
        case 'SQL': {
          this.$refs.TransformSqlModel.add()
          break
        }
      }
    },
    editTransform(transform, index) {
      switch (transform.transformMode) {
        case 'WITHOUT': {
          break
        }
        case 'SCRIPT': {
          this.$refs.TransformScriptModel.edit(transform, index)
          break
        }
        case 'PLUGIN': {
          this.$refs.TransformPluginModel.edit(transform, index)
          break
        }
        case 'SQL': {
          this.$refs.TransformSqlModel.edit(transform, index)
          break
        }
      }
    },
    deleteTransform(index) {
      this.modal.transformList.splice(index, 1)
    },
    handleAddTransform(transform) {
      let ids = this.modal.transformList.map(x => x.transformRuntimeId.split('_')[2])
      postAction(this.url.createId, ids).then((res) => {
        // 移除透传
        let withoutIndex = this.modal.transformList.findIndex(item => item.transformMode === 'WITHOUT')
        if (withoutIndex >= 0) this.modal.transformList.splice(withoutIndex, 1)

        transform.transformRuntimeId = 'transform_' + transform.transformMode.toLowerCase() + '_' + res.data.id
        this.modal.transformList.push(transform)
      })
    },
    handleUpdateTransform(transform, index) {
      this.$set(this.modal.transformList, index, transform)
    },

    // 保存规则
    saveRule() {
      if (!this.modal.sourceResourceList || this.modal.sourceResourceList.length === 0) {
        this.$message.error('至少添加一个数据源')
        return
      }
      if (!this.modal.transformList || this.modal.transformList.length === 0) {
        this.$message.error('至少添加一个数据转换')
        return
      }
      if (!this.modal.destResourceList || this.modal.destResourceList.length === 0) {
        this.$message.error('至少添加一个目标资源')
        return
      }
      const that = this
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          that.confirmLoading = true
          let rule = JSON.parse(JSON.stringify(this.modal))
          let obj
          if (this.ruleId) {
            obj = putAction(this.url.update, rule)
          } else {
            obj = postAction(this.url.add, rule)
          }
          obj.then(res => {
            if (res.code === 200) {
              that.$message.success('保存成功')
              that.onClose()
            } else {
              that.$message.error('保存失败')
            }
          })
            .finally(() => {
              that.confirmLoading = false
              that.onClose()
            })

        } else {
          return false
        }
      })
    },
    onClose() {
      this.$router.push({ name: 'ruleList' })
    },
    subScript(content) {
      let start = content.indexOf('function')
      let end = start + 100
      return content.substring(start, content.length > end ? end : content.length)
    }

  }
}
</script>

<style>

.title {
  font-size: 16px;
  font-weight: bold;
  color: rgba(0, 0, 0, 0.85);
  margin-bottom: 16px;
}

</style>



