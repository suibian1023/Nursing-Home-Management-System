<template>
  <div class="page-container" v-loading="loading">
    <el-card>
      <div class="control-panel">
        <div class="floor-selector">
          <label for="floorSelect">楼层:</label>
          <el-select id="floorSelect" v-model="selectedFloor" style="width:150px">
            <el-option v-for="floor in floors" :key="floor" :label="floor" :value="floor" />
          </el-select>
        </div>

        <div class="status-overview">
          <div class="status-item total">
            <span class="label">总床数</span>
            <span class="value">{{ statusCounts.total }}</span>
          </div>
          <div class="status-item occupied">
            <span class="label">有人</span>
            <span class="value">{{ statusCounts.occupied }}</span>
          </div>
          <div class="status-item vacant">
            <span class="label">空闲</span>
            <span class="value">{{ statusCounts.vacant }}</span>
          </div>
          <div class="status-item cleaning">
            <span class="label">待打扫</span>
            <span class="value">{{ statusCounts.cleaning }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-card style="margin-top:16px; flex:1">
      <div class="bed-grid-container">
        <div class="bed-grid">
          <div
            v-for="(item, index) in floorPlan"
            :key="index"
            class="grid-item"
          >
            <div class="room-card">
              <h3 class="room-number">{{ item.roomNumber }}</h3>
              <div class="bed-list">
                <div
                  v-for="bed in item.beds"
                  :key="bed.bedId"
                  class="bed-card"
                  :class="bed.status"
                  @click="viewBedDetails(bed)"
                >
                  <div class="bed-id">{{ bed.bedId }}</div>
                  <div class="bed-status-icon">
                    <span v-if="bed.status === 'occupied'" class="icon-occupied">&#9679;</span>
                    <span v-else-if="bed.status === 'cleaning'" class="icon-cleaning">&#9679;</span>
                    <span v-else class="icon-vacant">&#9675;</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { getBedList, getRoomList } from '@/api';

const loading = ref(false);
const selectedFloor = ref('一楼');
const floors = ['一楼', '二楼', '三楼'];

const floorMap = { '一楼': '1层', '二楼': '2层', '三楼': '3层' };

const allRooms = ref([]);
const allBeds = ref([]);

const bedStatusMap = { 0: 'vacant', 1: 'occupied', 2: 'cleaning' };

const floorPlan = computed(() => {
  const dbFloor = floorMap[selectedFloor.value];
  const rooms = allRooms.value.filter(r => r.floor === dbFloor);
  rooms.sort((a, b) => a.roomNo.localeCompare(b.roomNo));

  return rooms.map(room => {
    const beds = allBeds.value
      .filter(b => b.roomNo === room.roomNo)
      .map(b => ({
        bedId: b.bedNo,
        status: bedStatusMap[b.isUsed] || 'vacant'
      }))
      .sort((a, b) => a.bedId.localeCompare(b.bedId));

    return {
      roomNumber: room.roomNo,
      beds
    };
  });
});

const statusCounts = computed(() => {
  const dbFloor = floorMap[selectedFloor.value];
  const floorRoomNos = new Set(
    allRooms.value.filter(r => r.floor === dbFloor).map(r => r.roomNo)
  );
  const floorBeds = allBeds.value.filter(b => floorRoomNos.has(b.roomNo));

  return {
    total: floorBeds.length,
    occupied: floorBeds.filter(b => b.isUsed === 1).length,
    vacant: floorBeds.filter(b => b.isUsed === 0).length,
    cleaning: floorBeds.filter(b => b.isUsed === 2).length
  };
});

const fetchData = async () => {
  loading.value = true;
  try {
    const [roomRes, bedRes] = await Promise.all([
      getRoomList(),
      getBedList()
    ]);
    allRooms.value = roomRes.data || [];
    allBeds.value = bedRes.data || [];
  } catch (e) {
    console.error('获取床位数据失败:', e);
  } finally {
    loading.value = false;
  }
};

const statusLabel = (s) => {
  if (s === 'occupied') return '有人';
  if (s === 'cleaning') return '待打扫';
  return '空闲';
};

const viewBedDetails = (bed) => {
  alert(`查看床位详情:\nID: ${bed.bedId}\n状态: ${statusLabel(bed.status)}`);
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.control-panel {
  display: flex;
  justify-content: flex-start;
  gap: 30px;
  align-items: center;
}

.floor-selector label {
  margin-right: 10px;
  color: #5f5f5d;
  font-size: 14px;
  font-weight: 500;
}

.status-overview {
  display: flex;
  gap: 16px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background-color: #fcfbf8;
  border: 1px solid #eceae4;
  border-radius: 8px;
}

.status-item .label {
  font-size: 13px;
  color: #5f5f5d;
  font-weight: 500;
}

.status-item .value {
  font-size: 18px;
  font-weight: 600;
}

.status-item.occupied .value {
  color: #c0392b;
}

.status-item.vacant .value {
  color: #5f5f5d;
}

.status-item.cleaning .value {
  color: #b8860b;
}

.status-item.total .value {
  color: #1c1c1c;
}

.bed-grid-container {
  overflow-x: auto;
}

.bed-grid {
  display: grid;
  grid-template-columns: repeat(10, minmax(110px, 1fr));
  gap: 12px;
}

.grid-item {
  border-radius: 12px;
  background-color: #fcfbf8;
  border: 1px solid #eceae4;
  overflow: hidden;
}

.room-card {
  padding: 10px;
}

.room-number {
  font-size: 14px;
  color: #1c1c1c;
  font-weight: 600;
  letter-spacing: -0.5px;
  border-bottom: 1px solid #eceae4;
  padding-bottom: 6px;
  margin-bottom: 8px;
}

.bed-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.bed-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 8px;
  border: 1px solid #eceae4;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
  background-color: #fcfbf8;
}

.bed-card:hover {
  background-color: #faf8f3;
}

.bed-card.occupied {
  border-left: 3px solid rgba(192,57,43,0.4);
}

.bed-card.vacant {
  border-left: 3px solid rgba(45,106,79,0.4);
}

.bed-card.cleaning {
  border-left: 3px solid rgba(184,134,11,0.4);
}

.bed-id {
  font-size: 12px;
  color: #5f5f5d;
  font-weight: 500;
}

.bed-status-icon {
  font-size: 14px;
}

.icon-occupied {
  color: #c0392b;
}

.icon-cleaning {
  color: #b8860b;
}

.icon-vacant {
  color: #2d6a4f;
}

@media (max-width: 1400px) {
  .bed-grid {
    grid-template-columns: repeat(7, 1fr);
  }
}

@media (max-width: 1000px) {
  .bed-grid {
    grid-template-columns: repeat(5, 1fr);
  }
}

@media (max-width: 700px) {
  .bed-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .control-panel {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
