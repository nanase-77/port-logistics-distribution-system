<template>
  <div>
    <el-card class="search-card">
      <el-input
        v-model="searchContent"
        placeholder="输入货物描述查询"
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
          <span>集装箱管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="openAddModal">新增集装箱</el-button>
          </div>
        </div>
      </template>
      <el-table :data="filteredContainers" stripe style="width: 100%;">
        <el-table-column label="序号" width="80" type="index" :index="(index) => index + 1" />
        <el-table-column label="货物描述">
          <template #default="{ row }">{{ row.content || '<暂无>' }}</template>
        </el-table-column>
        <el-table-column prop="capacity" label="容量(TEU)" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(getStateText(row.status))">{{ getStateText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditModal(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑集装箱' : '新增集装箱'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="容量(TEU)">
          <el-input-number v-model="form.capacity" :min="1" :max="100" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%;">
            <el-option label="空闲" :value="0" />
            <el-option label="使用中" :value="1" />
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
import { getContainers, addContainer, updateContainer, deleteContainer } from '@/api/containers'

const showModal = ref(false)
const isEdit = ref(false)
const searchContent = ref('')

const containers = ref([])

const fetchData = async () => {
  try {
    const res = await getContainers()
    containers.value = res.records || res || []
  } catch {
    ElMessage.error('获取集装箱列表失败')
  }
}

onMounted(() => {
  fetchData()
})

const getStateText = (state) => {
  const stateStr = String(state)
  const stateMap = {
    '0': '空闲',
    '1': '使用中'
  }
  return stateMap[stateStr] || '未知'
}

const getStatusType = (status) => {
  const statusMap = {
    '空闲': 'success',
    '使用中': 'primary',
    '未知': 'info'
  }
  return statusMap[status] || 'info'
}

const filteredContainers = computed(() => {
  if (!searchContent.value) return containers.value
  return containers.value.filter(item => item.content && item.content.includes(searchContent.value))
})

const handleSearch = () => {
  if (searchContent.value) {
    const found = containers.value.find(item => item.content && item.content.includes(searchContent.value))
    if (!found) {
      ElMessage.warning('未找到该集装箱')
    }
  }
}

const form = reactive({
  id: null,
  capacity: 1,
  status: 0
})

const resetForm = () => {
  form.id = null
  form.capacity = 1
  form.status = 0
}

const openAddModal = () => {
  isEdit.value = false
  resetForm()
  showModal.value = true
}

const openEditModal = (row) => {
  isEdit.value = true
  form.id = row.id
  form.capacity = row.capacity || 1
  form.status = row.status
  showModal.value = true
}

const handleSave = async () => {
  try {
    if (isEdit.value) {
      await updateContainer({ id: form.id, capacity: form.capacity, status: form.status })
      ElMessage.success('集装箱信息已更新')
    } else {
      await addContainer({ capacity: form.capacity, status: form.status })
      ElMessage.success('集装箱已创建')
    }
    showModal.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该集装箱吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteContainer(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    if (ElMessageBox.close) {
      ElMessage.info('已取消删除')
    }
  }
}
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}

.table-card {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 8px;
}
</style>