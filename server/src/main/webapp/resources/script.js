var refreshPageInterval = 5000; //30 seconds
var contextPath;

function refreshRunningJobs() {
  console.info('refresh running jobs');

  $.ajax({
    url : contextPath + '/runningJobs',
    cache : false,
    success : function(response) {
      $("#runningJobsContent").html(response);
    },
    error : function(XMLHttpRequest, textStatus, errorThrown) {
      console.log(errorThrown);
    }
  });
}