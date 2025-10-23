
const HOST = "124.223.95.23:721"

const API_URL = "http://"+ HOST + "/rank-vote/api/get-vote-optionals";
const SUBMIT_URL = "http://"+ HOST + "/rank-vote/api/commit-rank-vote";

const voteList = [];
const songListEl = document.getElementById("songList");
const voteListEl = document.getElementById("voteList");
const voteCountEl = document.getElementById("voteCount");
const submitBtn = document.getElementById("submit-btn");
const rulesBtn = document.getElementById("rules-btn");
const rulesModal = document.getElementById("rules-modal");
const closeModal = document.querySelector(".close");

const PAGE_SIZE = 3;
let currentPage = 1;
let allSongs = [];

document.addEventListener('DOMContentLoaded', function () {
    rulesModal.style.display = "block";
});


// 获取歌曲列表
async function fetchSongs() {
    try {
        const res = await fetch(API_URL);
        const json = await res.json();
        if (json.code === 200) {
            allSongs = json.data;
            renderPagination();
            renderSongsPage(currentPage);
        } else {
            songListEl.innerHTML = "<p>获取歌曲失败</p>";
        }
    } catch (err) {
        songListEl.innerHTML = "<p>网络错误</p>";
    }
}

// 渲染歌曲分页
function renderSongsPage(page) {
    songListEl.innerHTML = "";
    const start = (page - 1) * PAGE_SIZE;
    const end = start + PAGE_SIZE;
    const pageSongs = allSongs.slice(start, end);

    pageSongs.forEach(song => {
        const card = document.createElement("div");
        card.className = "song-card";

        const title = document.createElement("strong");
        title.textContent = `${song.song_name}`;

        const info = document.createElement("div");
        info.innerHTML = `${song.game_name} (${song.year})<br/>得分：${(parseFloat(song.score)*100).toFixed(2) }，投票数：${song.vote_count}`;

        const iframe = document.createElement("iframe");
        iframe.src = `http://music.163.com/outchain/player?type=2&id=${song.iframe_url}&auto=0&height=66`;
        iframe.frameBorder = "0";

        const btn = document.createElement("button");
        btn.textContent = voteList.includes(song.id) ? "已加入" : "加入投票";
        btn.disabled = voteList.includes(song.id);
        btn.setAttribute("data-id", song.id);
        btn.addEventListener("click", (e) => {
            // 检查是否已达到20首限制
            if (voteList.length >= 20) {
                alert("最多只能选择20首歌曲！");
                return;
            }

            if (!voteList.includes(song.id)) {
                voteList.push(song.id);
                renderVotes(allSongs);
                btn.disabled = true;
                btn.textContent = "已加入";
            }
        });

        card.appendChild(title);
        card.appendChild(info);
        card.appendChild(iframe);
        card.appendChild(btn);
        songListEl.appendChild(card);
    });

    renderPagination();
}

// 渲染分页按钮
function renderPagination() {
    const totalPages = Math.ceil(allSongs.length / PAGE_SIZE);
    const paginationEl = document.createElement("div");
    paginationEl.className = "pagination";

    const prevBtn = document.createElement("button");
    prevBtn.textContent = "上一页";
    prevBtn.disabled = currentPage === 1;
    prevBtn.addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            renderSongsPage(currentPage);
        }
    });

    const nextBtn = document.createElement("button");
    nextBtn.textContent = "下一页";
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.addEventListener("click", () => {
        if (currentPage < totalPages) {
            currentPage++;
            renderSongsPage(currentPage);
        }
    });

    const pageInfo = document.createElement("span");
    pageInfo.textContent = `第 ${currentPage} 页 / 共 ${totalPages} 页`;

    paginationEl.appendChild(prevBtn);
    paginationEl.appendChild(pageInfo);
    paginationEl.appendChild(nextBtn);

    songListEl.appendChild(paginationEl);
}

// 渲染投票列表
// 渲染投票列表 - 优化版本
function renderVotes(allSongs) {
    voteListEl.innerHTML = "";

    if (voteList.length === 0) {
        const emptyMsg = document.createElement("div");
        emptyMsg.className = "empty-vote-list";
        emptyMsg.textContent = "暂无歌曲，请从左侧选择歌曲添加到投票列表";
        emptyMsg.style.padding = "1rem";
        emptyMsg.style.textAlign = "center";
        emptyMsg.style.color = "#7f8c8d";
        voteListEl.appendChild(emptyMsg);
    } else {
        voteList.forEach((id, index) => {
            const song = allSongs.find(s => s.id === id);
            if (!song) return;

            const item = document.createElement("div");
            item.className = "vote-item";
            item.draggable = true;
            item.dataset.index = index;

            const text = document.createElement("span");
            text.textContent = `${index + 1}. ${song.song_name} - ${song.game_name}`;

            const removeBtn = document.createElement("button");
            removeBtn.textContent = "❌ 撤销";
            removeBtn.addEventListener("click", (e) => {
                e.stopPropagation(); // 防止事件冒泡影响拖拽
                voteList.splice(index, 1);
                renderVotes(allSongs);
                updateSongButtons();
            });

            item.appendChild(text);
            item.appendChild(removeBtn);
            voteListEl.appendChild(item);
        });
    }

    voteCountEl.textContent = voteList.length;
    // 修改：至少选择一首歌即可提交
    submitBtn.disabled = voteList.length < 1;

    // 当达到20首时显示警告
    if (voteList.length === 20) {
        voteCountEl.style.color = "#e74c3c";
        voteCountEl.style.fontWeight = "bold";
    } else {
        voteCountEl.style.color = "";
        voteCountEl.style.fontWeight = "";
    }
}
// 更新歌曲按钮状态
function updateSongButtons() {
    document.querySelectorAll(".song-card button").forEach(btn => {
        const id = parseInt(btn.getAttribute("data-id"));
        if (voteList.includes(id)) {
            btn.disabled = true;
            btn.textContent = "已加入";
        } else {
            btn.disabled = false;
            btn.textContent = "加入投票";
        }
    });
}

// 提交投票
async function submitVote() {
    // 修改：至少选择一首歌即可提交
    if (voteList.length < 1) {
        alert("请至少选择1首歌曲后再提交");
        return;
    }

    // 确认提示
    const confirmed = confirm("投票后无法更改，是否确认提交？");
    if (!confirmed) {
        return;
    }

    // 获取用户ID
    const userId = prompt("请输入您的用户ID：");
    if (!userId) {
        alert("用户ID不能为空");
        return;
    }

    // 构建请求数据
    const requestData = {
        rankVoteDto: {
            songIdRanks: voteList.map((songId, index) => ({
                songId: songId,
                rank: index + 1  // 按照索引来
            }))
        },
        userId: userId
    };

    try {
        submitBtn.disabled = true;
        submitBtn.textContent = "提交中...";

        const response = await fetch(SUBMIT_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestData)
        });

        const result = await response.json();

        if (result.code === 200 && result.data === true) {
            alert("投票提交成功！");
            // 清空投票列表
            voteList.length = 0;
            renderVotes(allSongs);
            updateSongButtons();
            location.href = location.href;

        } else {
            alert(`投票失败：${result.message || "未知错误"}`);
        }
    } catch (error) {
        alert("网络错误，请稍后重试");
        console.error("提交投票错误:", error);
    } finally {
        submitBtn.disabled = voteList.length < 1;
        submitBtn.textContent = "提交投票";
    }
}

// 拖拽排序功能 - 优化版本
let dragSrcIndex = null;
let dragOverIndex = null;

voteListEl.addEventListener("dragstart", (e) => {
    const item = e.target.closest('.vote-item');
    if (item) {
        dragSrcIndex = +item.dataset.index;
        item.classList.add('dragging');
        e.dataTransfer.effectAllowed = 'move';
        e.dataTransfer.setData('text/plain', dragSrcIndex);
    }
});

voteListEl.addEventListener("dragover", (e) => {
    e.preventDefault();
    const item = e.target.closest('.vote-item');
    if (item) {
        dragOverIndex = +item.dataset.index;

        // 移除所有的高亮
        document.querySelectorAll('.vote-item.drag-over').forEach(el => {
            el.classList.remove('drag-over');
        });

        // 添加放置位置高亮
        const rect = item.getBoundingClientRect();
        const midpoint = rect.top + rect.height / 2;

        if (e.clientY < midpoint) {
            item.classList.add('drag-over-top');
        } else {
            item.classList.add('drag-over-bottom');
        }
    } else if (e.target === voteListEl && voteListEl.children.length === 0) {
        // 如果列表为空，允许拖拽到列表容器
        dragOverIndex = 0;
    }
});

voteListEl.addEventListener("dragenter", (e) => {
    e.preventDefault();
});

voteListEl.addEventListener("dragleave", (e) => {
    // 移除所有高亮
    document.querySelectorAll('.vote-item.drag-over-top, .vote-item.drag-over-bottom').forEach(el => {
        el.classList.remove('drag-over-top', 'drag-over-bottom');
    });
});

voteListEl.addEventListener("drop", (e) => {
    e.preventDefault();

    // 移除所有高亮
    document.querySelectorAll('.vote-item.dragging, .vote-item.drag-over-top, .vote-item.drag-over-bottom').forEach(el => {
        el.classList.remove('dragging', 'drag-over-top', 'drag-over-bottom');
    });

    if (dragSrcIndex === null || dragOverIndex === null) return;

    const item = e.target.closest('.vote-item');
    if (item) {
        const rect = item.getBoundingClientRect();
        const midpoint = rect.top + rect.height / 2;

        // 确定插入位置
        let targetIndex;
        if (e.clientY < midpoint) {
            targetIndex = dragOverIndex;
        } else {
            targetIndex = dragOverIndex + 1;
        }

        // 调整目标索引，确保不会超出范围
        targetIndex = Math.max(0, Math.min(targetIndex, voteList.length));

        // 如果拖拽到自身或相同位置，不做任何操作
        if (dragSrcIndex === targetIndex || dragSrcIndex === targetIndex - 1) {
            return;
        }

        // 执行数组重新排序
        const movedItem = voteList.splice(dragSrcIndex, 1)[0];

        // 调整目标索引，因为我们已经从数组中移除了一个元素
        if (targetIndex > dragSrcIndex) {
            targetIndex--;
        }

        voteList.splice(targetIndex, 0, movedItem);
        renderVotes(allSongs);
    }

    dragSrcIndex = null;
    dragOverIndex = null;
});

voteListEl.addEventListener("dragend", (e) => {
    // 移除所有高亮
    document.querySelectorAll('.vote-item.dragging, .vote-item.drag-over-top, .vote-item.drag-over-bottom').forEach(el => {
        el.classList.remove('dragging', 'drag-over-top', 'drag-over-bottom');
    });

    dragSrcIndex = null;
    dragOverIndex = null;
});

// 规则弹窗功能
rulesBtn.addEventListener("click", () => {
    rulesModal.style.display = "block";
});

closeModal.addEventListener("click", () => {
    rulesModal.style.display = "none";
});

window.addEventListener("click", (e) => {
    if (e.target === rulesModal) {
        rulesModal.style.display = "none";
    }
});

// 事件监听
submitBtn.addEventListener("click", submitVote);

// 初始化
fetchSongs();