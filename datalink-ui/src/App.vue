<template>
  <a-config-provider :locale="locale">
    <div id="app">
      <router-view/>
    </div>
  </a-config-provider>
</template>

<script>
import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN';

export default {
  data () {
    return {
      locale:zhCN
    }
  },
}
</script>

<style>

.ant-card-head{
  font-size: 15px !important;
}

.ant-layout-footer{
 padding: 0 !important;
}

.ant-card-bordered{
  border-radius: 3px !important;
}

</style>
