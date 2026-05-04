<template>
  <div>
    <el-card class="search-card">
      <el-input 
        v-model="searchOrderNumber" 
        placeholder="输入订单号查询" 
        style="width: 300px;"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <div class="header-actions">
            <el-select v-model="statusFilter" placeholder="筛选状态" style="width: 140px; margin-right: 10px;">
              <el-option label="全部" value="" />
              <el-option label="待处理" value="待处理" />
              <el-option label="进行中" value="进行中" />
              <el-option label="已完成" value="已完成" />
            </el-select>
            <el-button type="primary" @click="openAddModal">新增订单</el-button>
          </div>
        </div>
      </template>
      <el-table :data="filteredOrders" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderNumber" label="订单号" width="160" />
        <el-table-column label="用户" width="120">
          <template #default="{ row }">{{ getUsername(row.userId) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="关联集装箱" width="200">
          <template #default="{ row }">{{ getContainerContents(row.containerIds) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" min-width="280">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditModal(row)">编辑</el-button>
            <el-button v-if="row.status === '待处理'" type="success" size="small" @click="handleAudit(row)">审核通过</el-button>
            <el-button v-if="row.status === '进行中'" type="warning" size="small" @click="handleComplete(row)">标记完成</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑订单' : '新增订单'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="form.orderNumber" :disabled="isEdit" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="用户">
          <el-select v-model="form.userId" style="width: 100%;">
            <el-option v-for="user in users" :key="user.id" :label="user.username" :value="user.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联集装箱">
          <el-input v-model="form.containerIds" placeholder="请输入集装箱ID，多个用逗号分隔" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="form.status" style="width: 100%;">
            <el-option label="待处理" value="待处理" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const showModal = ref(false)
const isEdit = ref(false)
const statusFilter = ref('')
const searchOrderNumber = ref('')

const users = ref([])

const containers = ref([])

const orders = ref([])

const getUsername = (userId) => {
  const user = users.value.find(u => u.id === userId)
  return user ? user.username : '未知用户'
}

const getContainerContents = (containerIds) => {
  if (!containerIds) return ''
  const ids = containerIds.split(',').map(id => parseInt(id))
  const contents = ids.map(id => {
    const container = containers.value.find(c => c.id === id)
    return container ? container.content : `集装箱${id}`
  })
  return contents.join(', ')
}

const filteredOrders = computed(() => {
  let result = orders.value
  if (statusFilter.value) {
    result = result.filter(item => item.status === statusFilter.value)
  }
  if (searchOrderNumber.value) {
    result = result.filter(item => item.orderNumber.includes(searchOrderNumber.value))
  }
  return result
})

const handleSearch = () => {
  if (searchOrderNumber.value) {
    const found = orders.value.find(item => item.orderNumber.includes(searchOrderNumber.value))
    if (!found) {
      ElMessage.warning('未找到该订单')
    }
  }
}

const form = reactive({
  id: null,
  orderNumber: '',
  userId: 1,
  containerIds: '',
  status: '待处理'
})

const getStatusType = (status) => {
  const statusMap = {
    '待处理': 'warning',
    '进行中': 'primary',
    '已完成': 'success'
  }
  return statusMap[status] || 'info'
}

const resetForm = () => {
  form.id = null
  form.orderNumber = ''
  form.userId = 1
  form.containerIds = ''
  form.status = '待处理'
}

const openAddModal = () => {
  isEdit.value = false
  resetForm()
  showModal.value = true
}

const openEditModal = (row) => {
  isEdit.value = true
  form.id = row.id
  form.orderNumber = row.orderNumber
  form.userId = row.userId
  form.containerIds = row.containerIds
  form.status = row.status
  showModal.value = true
}

const handleSave = () => {
  if (!form.orderNumber) {
    ElMessage.warning('请输入订单号')
    return
  }
  if (isEdit.value) {
    const idx = orders.value.findIndex(o => o.id === form.id)
    if (idx !== -1) {
      orders.value[idx] = {
        ...orders.value[idx],
        userId: form.userId,
        containerIds: form.containerIds,
        status: form.status
      }
    }
    ElMessage.success('订单信息已更新')
  } else {
    orders.value.push({
      id: orders.value.length > 0 ? Math.max(...orders.value.map(o => o.id)) + 1 : 1,
      orderNumber: form.orderNumber,
      userId: form.userId,
      containerIds: form.containerIds,
      status: form.status,
      createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
    })
    ElMessage.success('订单已创建')
  }
  showModal.value = false
}

const handleAudit = (row) => {
  row.status = '进行中'
  ElMessage.success(`订单 ${row.orderNumber} 已审核通过`)
}

const handleComplete = (row) => {
  row.status = '已完成'
  ElMessage.success(`订单 ${row.orderNumber} 已标记完成`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除订单 "${row.orderNumber}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    orders.value = orders.value.filter(o => o.orderNumber !== row.orderNumber)
    ElMessage.success('订单已删除')
  }).catch(() => {})
}
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}
</style>
