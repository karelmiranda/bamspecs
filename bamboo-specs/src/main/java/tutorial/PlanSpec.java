import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.BambooOid;
import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseNaming;
import com.atlassian.bamboo.specs.api.builders.notification.AnyNotificationType;
import com.atlassian.bamboo.specs.api.builders.notification.Notification;
import com.atlassian.bamboo.specs.api.builders.permission.DeploymentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.EnvironmentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.repository.VcsRepositoryIdentifier;
import com.atlassian.bamboo.specs.builders.notification.UserRecipient;
import com.atlassian.bamboo.specs.builders.task.ArtifactDownloaderTask;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.CleanWorkingDirectoryTask;
import com.atlassian.bamboo.specs.builders.task.DownloadItem;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.util.BambooServer;

@BambooSpec
public class PlanSpec {

    public Deployment rootObject() {
        final Deployment rootObject = new Deployment(new PlanIdentifier("AT", "DT")
                .oid(new BambooOid("82qmlf5lu70h")),
                "DEPLOY1")
                .oid(new BambooOid("8333kwcbbtac"))
                .releaseNaming(new ReleaseNaming("release-1")
                        .autoIncrement(true))
                .environments(new Environment("Environment")
                        .tasks(new CleanWorkingDirectoryTask(),
                                new ArtifactDownloaderTask()
                                        .description("Download release contents")
                                        .artifacts(new DownloadItem()
                                                .allArtifacts(true)),
                                new VcsCheckoutTask()
                                        .checkoutItems(new CheckoutItem()
                                                .repository(new VcsRepositoryIdentifier()
                                                        .name("Bamboo Specs"))))
                        .notifications(new Notification()
                                .type(new AnyNotificationType(new AtlassianModule("bamboo.deployments:deploymentStartedFinished")))
                                .recipients(new UserRecipient("admin"))));
        return rootObject;
    }

    public DeploymentPermissions deploymentPermission() {
        final DeploymentPermissions deploymentPermission = new DeploymentPermissions("DEPLOY")
                .permissions(new Permissions()
                        .userPermissions("admin", PermissionType.EDIT, PermissionType.VIEW)
                        .loggedInUserPermissions(PermissionType.VIEW)
                        .anonymousUserPermissionView());
        return deploymentPermission;
    }

    public EnvironmentPermissions environmentPermission1() {
        final EnvironmentPermissions environmentPermission1 = new EnvironmentPermissions("DEPLOY")
                .environmentName("Environment")
                .permissions(new Permissions()
                        .userPermissions("admin", PermissionType.EDIT, PermissionType.VIEW, PermissionType.BUILD)
                        .loggedInUserPermissions(PermissionType.VIEW)
                        .anonymousUserPermissionView());
        return environmentPermission1;
    }

    public static void main(String... argv) {
        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer("http://bamboo-server:8085");
        final PlanSpec planSpec = new PlanSpec();

        final Deployment rootObject = planSpec.rootObject();
        bambooServer.publish(rootObject);

        final DeploymentPermissions deploymentPermission = planSpec.deploymentPermission();
        bambooServer.publish(deploymentPermission);

        final EnvironmentPermissions environmentPermission1 = planSpec.environmentPermission1();
        bambooServer.publish(environmentPermission1);
    }
}