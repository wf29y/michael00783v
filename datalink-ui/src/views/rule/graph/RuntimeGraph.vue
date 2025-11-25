<template>
  <div>
    <div id='graph'></div>
  </div>

</template>

<script>
import { Graph, Path } from '@antv/x6'
import '@antv/x6-vue-shape'
import ResourceNode from './ResourceNode'
import TransformNode from './TransformNode'

let groups = {
  top: {
    position: 'top',
    attrs: {
      circle: {
        r: 4,
        magnet: true,
        stroke: '#C2C8D5',
        strokeWidth: 1,
        fill: '#fff'
      }
    }
  },
  bottom: {
    position: 'bottom',
    attrs: {
      circle: {
        r: 4,
        magnet: true,
        stroke: '#C2C8D5',
        strokeWidth: 1,
        fill: '#fff'
      }
    }
  },
  left: {
    position: 'left',
    attrs: {
      circle: {
        r: 4,
        magnet: true,
        stroke: '#C2C8D5',
        strokeWidth: 1,
        fill: '#fff'
      }
    }
  },
  right: {
    position: 'right',
    attrs: {
      circle: {
        r: 4,
        magnet: true,
        stroke: '#C2C8D5',
        strokeWidth: 1,
        fill: '#fff'
      }
    }
  }
}

export default {
  name: 'RuntimeGraph',
  data() {
    return {
      graph: null
    }
  },

  mounted() {
    this.graph = new Graph({
      container: document.getElementById('graph'),
      grid: true,
      autoResize: true
      // connecting: {
      //   connector: 'algo-connector'
      // }
    })

    Graph.registerVueComponent(
      'ResourceNode',
      {
        template: `
          <ResourceNode></ResourceNode>`,
        components: {
          ResourceNode
        }
      },
      true
    )

    Graph.registerVueComponent(
      'TransformNode',
      {
        template: `
          <TransformNode></TransformNode>`,
        components: {
          TransformNode
        }
      },
      true
    )

    // 注册 nodeTemplate 的链接线
    Graph.registerEdge(
      'dag-edge',
      {
        inherit: 'edge',
        attrs: {
          line: {
            stroke: '#7d7c7c',
            strokeWidth: 1
            // targetMarker: null
          }
        }
      },
      true
    )

  },
  methods: {
    init(rule, runtime) {
      let destList = rule.destResourceList
      let sourceList = rule.sourceResourceList

      let cells = []

      cells.push(this.graph.createNode({
        id: 'transform-node',
        shape: 'vue-shape',
        component: 'TransformNode',
        width: 180,
        height: 35,
        x: 400,
        y: 100,
        data: {
          rule: rule,
          transform: runtime.transformRuntime
        },
        ports: {
          groups: groups,
          items: [
            { id: 'transform-node-port-left', group: 'left' },
            { id: 'transform-node-port-right', group: 'right' }
          ]
        }
      }))

      let sourceStartY = (200 - (sourceList.length - 1) * 50) / 2

      for (let i = 0; i < sourceList.length; i++) {
        let sourceItem = sourceList[i]
        cells.push(this.graph.createNode({
          id: 'source-node' + i,
          shape: 'vue-shape',
          component: 'ResourceNode',
          width: 180,
          height: 35,
          x: 0,
          y: sourceStartY + (i * 50),
          data: {
            resource: sourceItem,
            runtime: runtime.sourceRuntimeList[sourceItem.resourceRuntimeId]
          },
          ports: {
            groups: groups,
            items: [
              { id: 'source-node-port-right', group: 'right' }
            ]
          }
        }))

        cells.push(this.graph.createEdge({
          id: 'source-transform-edge-' + i,
          shape: 'dag-edge',
          source: {
            cell: 'source-node' + i,
            port: 'source-node-port-right'
          },
          target: {
            cell: 'transform-node',
            port: 'transform-node-port-left'
          },
          zIndex: 0
        }))

      }

      let destStartY = (200 - (destList.length - 1) * 50) / 2

      for (let i = 0; i < destList.length; i++) {
        let destItem = destList[i]
        cells.push(this.graph.createNode({
          id: 'dest-node' + i,
          shape: 'vue-shape',
          component: 'ResourceNode',
          width: 180,
          height: 35,
          x: 800,
          y: destStartY + (i * 50),
          data: {
            resource: destItem,
            runtime: runtime.destRuntimeList[destItem.resourceRuntimeId]
          },
          ports: {
            groups: groups,
            items: [
              { id: 'dest-node-port-left', group: 'left' }
            ]
          }
        }))

        cells.push(this.graph.createEdge({
          id: 'dest-transform-edge-' + i,
          shape: 'dag-edge',
          source: {
            cell: 'transform-node',
            port: 'transform-node-port-right'
          },
          target: {
            cell: 'dest-node' + i,
            port: 'dest-node-port-left'
          },
          zIndex: 0
        }))

      }


      this.graph.resetCells(cells)
      this.graph.centerContent()

    }
  }
}
</script>

<style>

#graph {
  width: 100%;
  min-height: 350px;
}
</style>