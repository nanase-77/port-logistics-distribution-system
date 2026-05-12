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
      <el-table :data="filteredOrders" stripe style="width: 100%;">
        <el-table-column label="序号" width="80" type="index" :index="(index) => index + 1" />
        <el-table-column prop="orderNumber" label="订单号" />
        <el-table-column label="客户" width="120">
          <template #default="{ row }">{{ getUsername(row.userId) }}</template>
        </el-table-column>
        <el-table-column prop="containerIds" label="集装箱" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditModal(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showModal" :title="isEdit ? '修改订单' : '新增订单'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="form.orderNumber" />
        </el-form-item>
        <el-form-item label="客户">
          <el-select v-model="form.userId" style="width: 100%;">
            <el-option v-for="user in users" :key="user.id" :label="user.username" :value="user.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="集装箱">
          <el-select v-model="form.containerId" style="width: 100%;">
            <el-option v-for="container in containers" :key="container.id" :label="container.containerNumber" :value="container.id" />
          </el-select>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, addOrder, updateOrder, deleteOrder } from '@/api/adminOrders'
import { getUsers } from '@/api/adminUsers'
import { getContainers } from '@/api/adminContainers'

const showModal = ref(false)
const isEdit = ref(false)
const statusFilter = ref('')
const searchOrderNumber = ref('')

const users = ref([])
const containers = ref([])
const orders = ref([])

const form = reactive({
  id: null,
  orderNumber: '',
  userId: null,
  containerId: null,
  status: '待处理'
})

const fetchUsers = async () => {
  try {
    const res = await getUsers()
    users.value = res.records || res || []
  } catch { /* ignore */ }
}

const fetchContainers = async () => {
  try {
    const res = await getContainers()
    containers.value = res.records || res || []
  } catch { /* ignore */ }
}

const fetchData = async () => {
  try {
    const res = await getOrders()
    orders.value = res.records || res || []
  } catch {
    ElMessage.error('获取订单列表失败')
  }
}

onMounted(() => {
  fetchUsers()
  fetchContainers()
  fetchData()
})

const getUsername = (userId) => {
  const user = users.value.find(u => u.id === userId)
  return user ? user.username : '未知用户'
}

const getContainerNumber = (containerId) => {
  const container = containers.value.find(c => c.id === containerId)
  return container ? container.containerNumber : '未知集装箱'
}

const filteredOrders = computed(() => {
  let result = orders.value
  if (statusFilter.value) {
    result = result.filter(item => item.status === statusFilter.value)
  }
  if (searchOrderNumber.value) {
    result = result.filter(item => item.orderNumber?.includes(searchOrderNumber.value))
  }
  return result
})

const openAddModal = () => {
  isEdit.value = false
  form.id = null
  form.orderNumber = ''
  form.userId = null
  form.containerId = null
  form.status = '待处理'
  showModal.value = true
}

const openEditModal = (row) => {
  isEdit.value = true
  form.id = row.id
  form.orderNumber = row.orderNumber
  form.userId = row.userId
  form.containerId = row.containerId
  form.status = row.status
  showModal.value = true
}

const handleSave = async () => {
  try {
    if (isEdit.value) {
      await updateOrder(form)
      ElMessage.success('修改成功')
    } else {
      await addOrder(form)
      ElMessage.success('添加成功')
    }
    showModal.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteOrder(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    ElMessage.info('已取消删除')
  }
}

const handleSearch = () => {
  if (searchOrderNumber.value) {
    const found = orders.value.find(item => item.orderNumber?.includes(searchOrderNumber.value))
    if (!found) {
      ElMessage.warning('未找到该订单')
    }
  }
}
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.table-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .header-actions {
    display: flex;
    align-items: center;
  }
}
</style>
