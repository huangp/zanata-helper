var refreshPageInterval = 10000; //10 seconds
var contextPath;

function refreshRunningJobs() {
  console.debug('refresh running jobs');

  $.ajax({
    url : contextPath + '/runningJobs',
    type: 'GET',
    cache : false,
    success : function(response) {
      $("#runningJobsContent").html(response);
    },
    error : function(XMLHttpRequest, textStatus, errorThrown) {
      console.log(errorThrown);
    }
  });
}

function toggleSection(checkbox, sectionId) {
  $("#" + sectionId).toggleClass("is-hidden", !checkbox.checked);
}

/**
 *
 * @param list
 * @param type - 'trans' or 'repo'
 */
function onPluginChanged(list, type) {
  var selectedClass = list.options[list.selectedIndex].value;
  var pluginConfigContent = type === 'trans' ? $("#transServerSettings") : $("#sourceRepoSettings");

  $.ajax({
    url : contextPath + '/jobs/new/settings?selectedPlugin=' + selectedClass + '&type=' + type,
    type: 'GET',
    cache : false,
    success : function(response) {
      pluginConfigContent.html(response);
    },
    error : function(XMLHttpRequest, textStatus, errorThrown) {
      console.log(errorThrown);
    }
  });
}