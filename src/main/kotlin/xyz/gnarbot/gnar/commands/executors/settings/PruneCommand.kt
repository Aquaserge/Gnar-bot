package xyz.gnarbot.gnar.commands.executors.settings

import net.dv8tion.jda.core.Permission
import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.utils.Context
import java.time.OffsetDateTime

@Command(
        id = 57,
        aliases = arrayOf("prune", "purge", "delmessages", "delmsgs"),
        usage = "(2-100)",
        description = "Delete up to 100 messages.",
        category = Category.SETTINGS,
        scope = Scope.TEXT,
        permissions = arrayOf(Permission.MESSAGE_MANAGE)
)
class PruneCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.isEmpty()) {
            CommandDispatcher.sendHelp(context, info)
            return
        }

        val history = context.channel.history

        val amount = args[0].toIntOrNull()?.coerceIn(0, 100) ?: kotlin.run {
            context.send().error("Improper arguments supplies, must be a number between `2-100`.").queue()
            return
        }

        if (amount < 2) {
            context.send().error("You need to delete 2 or more messages to use this command.").queue()
            return
        }

        val time = OffsetDateTime.now().minusWeeks(2)

        history.retrievePast(amount).queue {
            val messages = it.filter { msg ->
                msg.creationTime.isAfter(time)
            }

            when {
                messages.size >= 2 -> {
                    context.message.textChannel.deleteMessages(messages).queue()
                    context.send().info("Attempted to delete **${messages.size}** messages.").queue()
                }
                messages.size == 1 -> {
                    messages.first().delete().queue()
                    context.send().info("Attempted to delete **1** messages.").queue()
                }
                else -> {
                    context.send().info("No messages were found (that are younger than 2 weeks).").queue()
                }
            }
        }
    }
}
